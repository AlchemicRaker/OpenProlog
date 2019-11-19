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

        // TODO: guarantee the sleep time. currently can be interrupted by signal events
        this.m_jipEngine.getOwner().waitingCall(timeout);

        return true;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return false;
    }
}
