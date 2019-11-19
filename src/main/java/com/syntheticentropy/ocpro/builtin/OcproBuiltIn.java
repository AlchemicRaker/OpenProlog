package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.*;

import java.util.stream.Collectors;

public abstract class OcproBuiltIn extends BuiltIn {

    public PrologObject rawToPrologObject(Object raw) {
        if (raw instanceof Boolean) {
            Boolean b = (Boolean) raw;
            return Atom.createAtom(((Boolean) raw) ? "true" : "false");
        }
        if (raw instanceof String) {
            // TODO: strings should probably be turned into ConsCell/ConsList?
            return Atom.createAtom((String) raw);
        }
        if (raw instanceof Integer) {
            return Expression.createNumber((Integer) raw);
        }
        if (raw instanceof Double) {
            return Expression.createNumber((Double) raw);
        }
        if (raw instanceof byte[]) {
            return Atom.createAtom(new String((byte[]) raw));
        }
        // TODO: add maps and lists support
        return Atom.createAtom(raw.toString());
    }

    public Object prologObjectToRaw(PrologObject prologObjectIndirect) {
        PrologObject prologObject = prologObjectIndirect instanceof Variable ?
                ((Variable) prologObjectIndirect).getObject() : prologObjectIndirect;
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
        if (prologObject instanceof ConsCell) {
            return ((ConsCell) prologObject).getTerms().stream()
                    .map(this::prologObjectToRaw).collect(Collectors.toList());
        }
        return "unable to decode";
    }
}
