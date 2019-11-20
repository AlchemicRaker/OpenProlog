package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.PString;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;

import java.util.Hashtable;

public class TermString2 extends OcproBuiltIn {

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {
        final PrologObject termParam = getParam(1);
        final PrologObject stringParam = getParam(2);


        final PrologObject termTerm = getRealTerm(getParam(1));
        final PrologObject stringTerm = getRealTerm(getParam(2));

        //(Term, String)
        //Convert term into a string, or parse string into a term

        if (stringTerm == null) { //solving for string
            PrologObject stringResult = new PString(termTerm.toString(getJIPEngine()), true);
            return stringResult.unify(stringParam, varsTbl);

        } else { //solving for term
            JIPTerm termResult = getJIPEngine().getTermParser().parseTerm(stringTerm.toString());
            return termResult.getTerm().unify(termParam, varsTbl);
        }
    }
}
