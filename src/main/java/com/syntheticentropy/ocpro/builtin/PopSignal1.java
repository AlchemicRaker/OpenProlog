package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.ConsList;
import com.ugos.jiprolog.engine.Functor;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;
import li.cil.oc.api.machine.Signal;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class PopSignal1 extends OcproBuiltIn {

    boolean hasMoreChoices = true;

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {
        final PrologObject signalParam = getParam(1);

        //(Timeout, Signal, [Args])

        // do a synchronized call to poll for a signal

        Signal signal = null;
        try {
            signal = this.getJIPEngine().getOwner().machine.popSignal();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (signal == null) {
            hasMoreChoices = false;
            return false;
        }

        hasMoreChoices = true;

        List<PrologObject> argsList = Arrays.asList(signal.args()).stream().map(this::rawToPrologObject).collect(Collectors.toList());

        Functor functor = new Functor(signal.name() + "/" + argsList.size(), ConsList.create(argsList).getConsCell());

        functor.unify(signalParam, varsTbl);
        return true;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return hasMoreChoices;
    }
}
