package com.syntheticentropy.ocpro.jipxcall;

import com.ugos.jiprolog.engine.BuiltIn;
import com.ugos.jiprolog.engine.Expression;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;

import java.util.Hashtable;

public class Sleep extends BuiltIn {

    @Override
    public boolean unify(Hashtable<Variable, Variable> m_varsTbl) {
        PrologObject time = null;

        time = getParam(1);
        PrologObject test = getRealTerm(time);

        this.m_jipEngine.getOwner().waitingCall((int) ((Expression) test).getValue());

        return false;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return false;
    }
}
