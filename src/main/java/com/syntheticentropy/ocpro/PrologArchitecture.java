package com.syntheticentropy.ocpro;

import com.ugos.jiprolog.engine.JIPDebugger;
import com.ugos.jiprolog.engine.JIPEngine;
import li.cil.oc.api.machine.Architecture;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Machine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Architecture.Name("Prolog")
public class PrologArchitecture implements Architecture {

    final Machine machine;

    JIPEngine jip;

    private Object vmReference;
    private Object synchronizedCallFn;
    private Object synchronizedResult;

    private List<PrologAPI> apis = Arrays.asList(
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
        JIPDebugger.debug = true; // force it to load .pl files instead of compiled files

        jip = new JIPEngine();

        // Inject java-backed APIs
        apis.forEach(PrologAPI::initialize);

        // load kernel
        InputStream machineKernel = machine.getClass().getResourceAsStream(OpenProlog.RESOURCE_PATH + "machine.pl");
        jip.consultStream(machineKernel, "kernel");

        return true;
    }

    @Override
    public void runSynchronized() {

    }

    @Override
    public ExecutionResult runThreaded(boolean b) {
        return null;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public boolean recomputeMemory(Iterable<ItemStack> iterable) {
        // TODO: care about memory limits
        return true;
    }

    @Override
    public void close() {
        vmReference = null;
        synchronizedCallFn = null;
        synchronizedResult = null;
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
