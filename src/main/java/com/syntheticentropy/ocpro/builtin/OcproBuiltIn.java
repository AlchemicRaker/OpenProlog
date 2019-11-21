package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.*;
import li.cil.oc.server.component.HandleValue;

import java.util.stream.Collectors;

public abstract class OcproBuiltIn extends BuiltIn {

    public PrologObject rawToPrologObject(Object raw) {
        if (raw instanceof Boolean) {
            Boolean b = (Boolean) raw;
            return Atom.createAtom(((Boolean) raw) ? "true" : "false");
        }
        if (raw instanceof String) {
            return new Functor(Atom.createAtom("string/1"), new PString((String) raw, true).getConsCell());
        }
        if (raw instanceof Integer) {
            return Expression.createNumber((Integer) raw);
        }
        if (raw instanceof Double) {
            return Expression.createNumber((Double) raw);
        }
        if (raw instanceof byte[]) {
            return new Functor(Atom.createAtom("bytes/1"), new PString(new String((byte[]) raw), true).getConsCell());
        }
        if (raw instanceof HandleValue) {
            return Expression.createNumber(((HandleValue) raw).handle());
        }
        if (raw == null) {
            return Atom.createAtom("null");
        }
        // TODO: add maps and lists support
        return Atom.createAtom(raw.toString());
    }

    public Object prologObjectToRaw(PrologObject prologObjectIndirect) {
        PrologObject prologObject = prologObjectIndirect instanceof Variable ?
                ((Variable) prologObjectIndirect).getObject() : prologObjectIndirect;

        if (prologObject == null) // Variable that is bound to nothing
            return null;

        if (prologObject instanceof Expression) {
            if (((Expression) prologObject).isInteger())
                return Math.floor(((Expression) prologObject).getValue());
            else
                return ((Expression) prologObject).getValue();
        }
        if (prologObject instanceof Atom) {
            String str = ((Atom) prologObject).getName();
            if (str.equalsIgnoreCase("true"))
                return Boolean.TRUE;
            else if (str.equalsIgnoreCase("false"))
                return Boolean.FALSE;
            else
                return str;
        }
        if (prologObject instanceof Functor) {
            Functor functor = (Functor) prologObject;
            if (functor.getFriendlyName().equalsIgnoreCase("string")) {
                return new PString(PString.create(((ConsCell) functor.getTerm(2).getRealTerm()).getTerms()), true)
                        .getString();
            }
            if (functor.getFriendlyName().equalsIgnoreCase("bytes")) {
                return new PString(PString.create(((ConsCell) functor.getTerm(2).getRealTerm()).getTerms()), true)
                        .getString().getBytes();
            }
        }
        if (prologObject instanceof ConsCell) {
            return ((ConsCell) prologObject).getTerms().stream()
                    .map(this::prologObjectToRaw).collect(Collectors.toList());
        }
        return "unable to decode";
    }
}
