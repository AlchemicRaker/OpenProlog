package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.Atom;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class ComponentType2 extends OcproBuiltIn {

    private Iterator<Map.Entry<String, String>> resultIterator = null;

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {

        final PrologObject typeParam = getParam(1);
        final PrologObject addressParam = getParam(2);
        // component_list(Type, Address)

        if (resultIterator == null) {

            Map<String, String> components = this.m_jipEngine.getOwner().machine.components();
            resultIterator = components.entrySet().iterator();

            // TODO: optimize this by filtering now, if available
        }

        while (resultIterator.hasNext()) {
            Map.Entry<String, String> next = resultIterator.next();
            PrologObject addressAtom = Atom.createAtom(next.getKey());
            PrologObject typeAtom = Atom.createAtom(next.getValue());

            boolean attempt = (addressAtom == null || addressAtom.unifiable(addressParam)) &&
                    (typeAtom == null || typeAtom.unifiable(typeParam));

            if (attempt) {

                // One result from this function = one call
                m_jipEngine.getOwner().synchronizedCall(() -> null);

                return (addressAtom == null || addressAtom.unify(addressParam, varsTbl)) &&
                        (typeAtom == null || typeAtom.unify(typeParam, varsTbl));
            }
        }

        // One result from this function = one call
        m_jipEngine.getOwner().synchronizedCall(() -> null);
        return false;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return resultIterator == null || resultIterator.hasNext();
    }
}
