package com.syntheticentropy.ocpro.builtin;

import com.ugos.jiprolog.engine.*;
import li.cil.oc.server.component.HandleValue;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class OcproBuiltIn extends BuiltIn {

    private ConsList entryToConsList(Map.Entry entry) {
        return ConsList.create(Arrays.asList(rawToPrologObject(entry.getKey()), rawToPrologObject(entry.getValue())));
    }
    private boolean isMap(Object o) {
        return o instanceof Map || o instanceof scala.collection.Map;
    }
    private Map<Object, Object> toMap(Object o) {
        if (o instanceof scala.collection.Map) {
            return (Map<Object, Object>) JavaConverters.mapAsJavaMapConverter((scala.collection.Map) o).asJava();
        }
        return (Map<Object, Object>) o;
    }

    @SuppressWarnings("unchecked")
    public PrologObject rawToPrologObject(Object raw) {
        if (raw instanceof Boolean) {
            Boolean b = (Boolean) raw;
            return Atom.createAtom(((Boolean) raw) ? "true" : "false");
        }
        if (raw instanceof String) {
            return stringToFunctor((String) raw, "string");
        }
        if (raw instanceof Integer) {
            return Expression.createNumber((Integer) raw);
        }
        if (raw instanceof Double) {
            return Expression.createNumber((Double) raw);
        }
        if (raw instanceof byte[]) {
            return stringToFunctor(new String((byte[]) raw), "bytes");
        }
        if (raw instanceof HandleValue) {
            return Expression.createNumber(((HandleValue) raw).handle());
        }
        if (isMap(raw)) {
            Map<Object, Object> map = toMap(raw);
            return ConsList.create(map.entrySet().stream().map(this::entryToConsList).collect(Collectors.toList()));
        }
        if (raw instanceof Object[]) {
            Object[] objects = (Object[]) raw;
            return ConsList.create(Arrays.stream(objects).map(this::rawToPrologObject).collect(Collectors.toList()));
        }
        if (raw instanceof double[]) {
            return ConsList.create(Arrays.stream((double[]) raw).boxed().map(this::rawToPrologObject).collect(Collectors.toList()));
        }
        if (raw instanceof int[]) {
            return ConsList.create(Arrays.stream((int[]) raw).boxed().map(this::rawToPrologObject).collect(Collectors.toList()));
        }
        if (raw == null) {
            return Atom.createAtom("null");
        }
        // TODO: add maps and lists support
        return Atom.createAtom(raw.toString());
    }

    public Functor stringToFunctor(String str, String functorName) {
        return new Functor(Atom.createAtom(functorName + "/1"), new ConsCell(new PString(PString.getList(str, true), true), null));
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
            if (functor.getTerm(2) instanceof Variable && !((Variable) functor.getTerm(2)).isBounded())
                return "[" + ((Variable) functor.getTerm(2)).getName() + "]";
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
