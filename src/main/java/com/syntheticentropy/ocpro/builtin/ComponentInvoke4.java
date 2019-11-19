package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.*;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.network.Node;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentInvoke4 extends OcproBuiltIn {

    @Override
    public boolean unify(Hashtable<Variable, Variable> varsTbl) {
        final PrologObject addressTerm = getRealTerm(getParam(1));
        final PrologObject methodTerm = getRealTerm(getParam(2));
        final PrologObject paramsTerm = getRealTerm(getParam(3));
        final PrologObject outputParam = getParam(4);

        //(Address, Method, [Args], [Output])

        if (!(addressTerm instanceof Atom))
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, addressTerm);

        final String address = ((Atom) addressTerm).getName();


        if (!(methodTerm instanceof Atom))
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, methodTerm);

        final String method = ((Atom) methodTerm).getName();

        if (!(paramsTerm instanceof ConsList))
            throw new JIPTypeException(JIPTypeException.LIST, paramsTerm);

        List<Object> params = ((ConsList) paramsTerm).getTerms().stream()
                .map(this::prologObjectToRaw).collect(Collectors.toList());


        // TODO: if it is a direct call, no need to send it through synchronizedCall()
        Node node = getJIPEngine().getOwner().machine.node().network().node(address);
        Callback callback = getJIPEngine().getOwner().machine.methods(node.host()).get(method);

        List<PrologObject> resultList = null;

        if (callback.direct()) {
            try {
                resultList = Arrays.asList(getJIPEngine().getOwner().machine
                        .invoke(address, method, params.toArray())).stream()
                        .map(this::rawToPrologObject)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resultList = Arrays.asList(
                    this.m_jipEngine.getOwner().synchronizedCall(() -> {
                        try {
                            return this.getJIPEngine().getOwner().machine.invoke(address, method, params.toArray());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new Object[0];
                    })
            ).stream()
                    .map(this::rawToPrologObject)
                    .collect(Collectors.toList());
        }

        ConsList results = ConsList.create(resultList);

        return results.unify(outputParam, varsTbl);
    }
}
