package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.Expression;
import com.ugos.jiprolog.engine.JIPTypeException;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;

import java.util.Hashtable;

public class Sleep2 extends OcproBuiltIn {

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {
        final PrologObject timeoutTerm = getRealTerm(getParam(1));
        final PrologObject waitedParam = getParam(2);

        if (!(timeoutTerm instanceof Expression) || !((Expression) timeoutTerm).isInteger())
            throw new JIPTypeException(JIPTypeException.INTEGER, timeoutTerm);

        final Integer timeout = (int) ((Expression) timeoutTerm).getValue();

        long waited = this.m_jipEngine.getOwner().waitingCall(timeout);

        Expression waitedResult = Expression.createNumber(waited);

        return waitedResult.unify(waitedParam, varsTbl);
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return false;
    }
}
