package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.Atom;
import com.ugos.jiprolog.engine.PrologObject;
import com.ugos.jiprolog.engine.Variable;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.Node;
import net.minecraft.util.Tuple;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentMethod2 extends OcproBuiltIn {

    private Iterator<Tuple<String, String>> resultIterator = null;

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {

        final PrologObject addressParam = getParam(1);
        final PrologObject methodParam = getParam(2);
        // component_method(Address, Method)

        if (resultIterator == null) {
            Machine machine = getJIPEngine().getOwner().machine;
            Map<String, String> components = machine.components();
            List<Tuple<String, String>> results = components.entrySet().stream()
                    .map(Map.Entry::getKey)
                    .flatMap(address -> {
                        Node node = machine.node().network().node(address);
                        if (node == null) return Stream.empty();
                        List<String> methods = machine.methods(node.host())
                                .entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
                        return methods.stream().map(method -> new Tuple<>(address, method));
                    })
                    .collect(Collectors.toList());

            resultIterator = results.iterator();

            // TODO: optimize this by filtering now, if available
        }

        while (resultIterator.hasNext()) {
            Tuple<String, String> next = resultIterator.next();
            PrologObject addressAtom = Atom.createAtom(next.getFirst());
            PrologObject methodAtom = Atom.createAtom(next.getSecond());

            boolean attempt = (addressAtom == null || addressAtom.unifiable(addressParam)) &&
                    (methodAtom == null || methodAtom.unifiable(methodParam));

            if (attempt) {
                // One result from this function = one call
//                getJIPEngine().getOwner().synchronizedCall(() -> null);

                return (addressAtom == null || addressAtom.unify(addressParam, varsTbl)) &&
                        (methodAtom == null || methodAtom.unify(methodParam, varsTbl));
            }
        }

        // One result from this function = one call
//        getJIPEngine().getOwner().synchronizedCall(() -> null);
        return false;
    }

    @Override
    public boolean hasMoreChoicePoints() {
        return resultIterator == null || resultIterator.hasNext();
    }
}
