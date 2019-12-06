package com.syntheticentropy.ocpro;

import com.google.common.collect.Lists;
import li.cil.oc.api.machine.Architecture;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Machine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Architecture.Name("Prolog")
public class PrologArchitecture implements Architecture {

    public final Machine machine;

    private PrologVM vm;

    private Supplier<Object[]> synchronizedSupplier;
    private Object[] synchronizedResult;
    private FutureTask<Object[]> synchronizedFutureTask;

    private VMResponse vmQueryResponse;
    private FutureTask<Object> vmQueryResponseFutureTask;
    private int vmQuerySleepTicks;

    public List<ExecutionResult> forceExecutionResult = Lists.newArrayList();

    enum VMResponse {
        RunSynchronous,
        Wait,
        Shutdown,
        Restart,
        None
    }

    protected List<PrologAPI> apis = Arrays.asList(
            new ComponentAPI(this), // Necessary for MVP
            new OSAPI(this) // Placeholder for future APIs
    );

    /**
     * The constructor must have exactly this signature.
     */
    public PrologArchitecture(Machine machine) {
        this.machine = machine;
    }

    @Override
    public boolean initialize() {

        //create the VM
        if (vm != null && vm.jip != null) vm.jip.releaseAllResources();
        forceExecutionResult = Lists.newArrayList();
        vm = new PrologVM(this);

        return true;
    }

    @Override
    public void runSynchronized() {
        synchronizedResult = synchronizedSupplier.get();
    }

    @Override
    public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
        try {
            return runThreadedMain(isSynchronizedReturn);
        } catch (Exception e) {
            return new ExecutionResult.Error(e.getMessage());
        }
    }

    public ExecutionResult runThreadedMain(boolean isSynchronizedReturn) {
        if (forceExecutionResult.size() > 0) {
            return forceExecutionResult.remove(0);
        }

        //Called for: synchronized return, init, or signal
        vmQueryResponse = VMResponse.None;
        vmQueryResponseFutureTask = new FutureTask<>(() -> "");

        switch (vm.getState()) {
            case NEW:
                // Run the EEPROM kernel & all that fun
                vm.start();
                break;
            case RUNNABLE:
                // It's already running, queue the message?
                return new ExecutionResult.Error("VM already running");
            case WAITING:
            case TIMED_WAITING:
                // Resume it with a value from sync?
                if (isSynchronizedReturn) {
                    // this is any component/invoke query
                    // update already prepared in runSynchronized
                    FutureTask<Object[]> tmpSyncTask = synchronizedFutureTask;

                    synchronizedFutureTask = null;
                    synchronizedSupplier = null;

                    tmpSyncTask.run();
                } else {
                    // this must be a wait() query
                    // let the wait query handle iterating through signals
                    // we just need to wake it up
                    FutureTask<Object[]> tmpSyncTask = synchronizedFutureTask;

                    synchronizedFutureTask = null;

                    tmpSyncTask.run();
                }
                break;
            case BLOCKED:
                // It's waiting on something specific, queue the message?
                return new ExecutionResult.Error("VM run while blocked");
            case TERMINATED:
                // Need a new VM
                return new ExecutionResult.Error("VM run while terminated");
        }
        // (magical time where prolog engine resolves part of the query)
        try {
            if (vm.exitException != null) {
                return new ExecutionResult.Error(vm.exitException.getMessage());
            }
            if (vm.getState() == Thread.State.TERMINATED) {
                return new ExecutionResult.Error("Terminated early");
            }
            vmQueryResponseFutureTask.get(30, TimeUnit.SECONDS);
//            vmQueryResponseFutureTask.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
            if (vm.exitException != null) {
                return new ExecutionResult.Error(vm.exitException.getMessage());
            }
            return new ExecutionResult.Error(e.getMessage());
        }

        // a query has sent us something (or the thread exited)
        if (vm.getState() == Thread.State.TERMINATED ||
                vmQueryResponse == VMResponse.Shutdown) {
            return new ExecutionResult.Shutdown(false);
        }
        if (vmQueryResponse == VMResponse.Restart) {
            return new ExecutionResult.Shutdown(true);
        }
        if (vmQueryResponse == VMResponse.Wait) {
            return new ExecutionResult.Sleep(vmQuerySleepTicks);
        }
        if (vmQueryResponse == VMResponse.RunSynchronous) {
            // by this point, synchronized- variables should all be set
            return new ExecutionResult.SynchronizedCall();
        }

        if (vm.exitException != null) {
            return new ExecutionResult.Error(vm.exitException.getMessage());
        }
        return new ExecutionResult.Error("Some problem eh? (2)");
    }

    // a query calls this
    public Object[] synchronizedCall(Supplier<Object[]> task) {
        synchronizedSupplier = task;
        synchronizedFutureTask = new FutureTask<>(() -> {
            Object[] result = synchronizedResult;
            synchronizedResult = null;
            return result;
        });

        // let the architecture know the query is ready and waiting
        vmQueryResponse = VMResponse.RunSynchronous;
        if (vmQueryResponseFutureTask == null) return null;
        vmQueryResponseFutureTask.run();

        try {
            // wait for the architecture to run the sync'd code *and* resume on the thread
            return synchronizedFutureTask.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            e.printStackTrace();
            crash(e.getMessage());
        }
        return null;
    }

    // a query calls this
    public long waitingCall(int ticks) {
        long startTime = machine.worldTime();
        synchronizedFutureTask = new FutureTask<>(() -> new Object[0]);

        // let the architecture know the query is waiting
        vmQueryResponse = VMResponse.Wait;
        vmQuerySleepTicks = ticks;
        if (vmQueryResponseFutureTask == null) return 0;
        vmQueryResponseFutureTask.run();

        try {
            // wait for the architecture to resume on the thread, presumably with a signal
            synchronizedFutureTask.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
//            e.printStackTrace();
        }
        long stopTime = machine.worldTime();
        return stopTime - startTime;
    }

    public void crash(String e) {
        forceExecutionResult.add(new ExecutionResult.Error(e));
        if (machine.isRunning()) {
            machine.crash(e);
            close();
        }
    }

    @Override
    public boolean isInitialized() {
        return vm != null && vm.getState() != Thread.State.NEW;
    }

    @Override
    public boolean recomputeMemory(Iterable<ItemStack> iterable) {
        // TODO: care about memory limits
        return true;
    }

    @Override
    public void close() {
        if (vm != null && vm.jip != null) vm.jip.releaseAllResources();
        if (vm != null) {
            vm.pleaseStop(true);
            vm.jip = null;
        }
        vm = null;

        synchronizedSupplier = null;
        synchronizedResult = null;
        if (synchronizedFutureTask != null && !synchronizedFutureTask.isDone() && !synchronizedFutureTask.isCancelled())
            synchronizedFutureTask.cancel(true);
        synchronizedFutureTask = null;

        vmQueryResponse = null;
        if (vmQueryResponseFutureTask != null && !vmQueryResponseFutureTask.isDone() && !vmQueryResponseFutureTask.isCancelled())
            vmQueryResponseFutureTask.cancel(true);
        vmQueryResponseFutureTask = null;
    }

    @Override
    public void onSignal() {
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void load(NBTTagCompound nbtTagCompound) {
        // Based on LuaJ behavior (no persistence)
        if (machine.isRunning()) {
            machine.stop();
            machine.start();
        }
    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {
        // Based on LuaJ behavior (no persistence)
    }
}
