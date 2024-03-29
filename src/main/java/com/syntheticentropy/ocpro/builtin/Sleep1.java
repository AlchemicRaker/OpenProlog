package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.Expression;
import com.ugos.jiprolog.engine.JIPTypeException;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;

import java.util.Hashtable;

public class Sleep1 extends OcproBuiltIn {

    @Override
    public boolean unify(Hashtable<Variable, Variable> m_varsTbl) {
        final PrologObject timeoutTerm = getRealTerm(getParam(1));

        if (!(timeoutTerm instanceof Expression) || !((Expression) timeoutTerm).isInteger())
            throw new JIPTypeException(JIPTypeException.INTEGER, timeoutTerm);

        final Integer timeout = (int) ((Expression) timeoutTerm).getValue();

        long waited = this.m_jipEngine.getOwner().waitingCall(timeout);

        return timeout.longValue() == waited; //true if that much time has passed
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return false;
    }
}
