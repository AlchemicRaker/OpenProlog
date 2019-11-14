package com.syntheticentropy.ocpro;

import li.cil.oc.api.machine.Architecture;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Machine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@Architecture.Name("Prolog")
public class PrologArchitecture implements Architecture {

    private final Machine machine;

    /**
     * The constructor must have exactly this signature.
     */
    public PrologArchitecture(Machine machine) {
        this.machine = machine;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public boolean recomputeMemory(Iterable<ItemStack> iterable) {
        return false;
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public void runSynchronized() {

    }

    @Override
    public ExecutionResult runThreaded(boolean b) {
        return null;
    }

    @Override
    public void onSignal() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
