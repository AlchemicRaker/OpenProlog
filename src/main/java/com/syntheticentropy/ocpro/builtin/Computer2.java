package com.syntheticentropy.ocpro.builtin;

import com.syntheticentropy.ocpro.PrologArchitecture;
import com.ugos.jiprolog.engine.Atom;
import com.ugos.jiprolog.engine.JIPTypeException;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;
import li.cil.oc.api.network.Connector;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class Computer2 extends OcproBuiltIn {
    private Map<String, Function<PrologArchitecture, Object>> exposedValues = createMap();

    private static Map<String, Function<PrologArchitecture, Object>> createMap() {
        Map<String, Function<PrologArchitecture, Object>> m = new HashMap<>();
        m.put("realTime", o -> System.currentTimeMillis() / 1000.0);
        m.put("uptime", o -> o.machine.upTime());
        m.put("address", o -> o.machine.node().address());
//        m.put("freeMemory", o -> o.)
//        m.put("totalMemory", o -> o.)
        m.put("energy", o -> ((Connector) o.machine.node()).globalBuffer());
        m.put("maxEnergy", o -> li.cil.oc.Settings.get().ignorePower() ?
                Double.POSITIVE_INFINITY :
                ((Connector) o.machine.node()).globalBufferSize());

        return m;
    }

    private Iterator<Map.Entry<String, Function<PrologArchitecture, Object>>> resultIterator = null;

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {
        final PrologObject keyParam = getParam(1);
        final PrologObject valueParam = getParam(2);

        if (resultIterator == null) {
            final PrologObject keyTerm = getRealTerm(keyParam);

            // we know that a provided key must be an atom
            if (!(keyTerm == null || keyTerm instanceof Atom))
                throw new JIPTypeException(JIPTypeException.ATOM, keyTerm);

            resultIterator = exposedValues.entrySet().iterator();
        }

        PrologArchitecture owner = m_jipEngine.getOwner();

        while (resultIterator.hasNext()) {
            Map.Entry<String, Function<PrologArchitecture, Object>> next = resultIterator.next();
            PrologObject keyAtom = Atom.createAtom(next.getKey());
            if (!(keyAtom == null || keyAtom.unifiable(keyParam))) continue;

            // don't evaluate the value until after we've matched a key
            PrologObject value = rawToPrologObject(next.getValue().apply(owner));
            if (!(value == null || value.unifiable(valueParam))) continue;

            return (keyAtom == null || keyAtom.unify(keyParam, varsTbl)) &&
                    (value == null || value.unify(valueParam, varsTbl));
        }

        return false;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return resultIterator == null || resultIterator.hasNext();
    }
}