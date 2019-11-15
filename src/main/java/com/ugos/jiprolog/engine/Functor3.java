/*
 * 23/04/2014
 *
 * Copyright (C) 1999-2014 Ugo Chirico - http://www.ugochirico.com
 *
 * This is free software; you can redistribute it and/or
 * modify it under the terms of the Affero GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.ugos.jiprolog.engine;

import java.util.Hashtable;

final class Functor3 extends BuiltIn {
    public final boolean unify(final Hashtable varsTbl) {
        PrologObject func = getRealTerm(getParam(1));

        if (func == null) {
            PrologObject name = getRealTerm(getParam(2));
            PrologObject arity = getRealTerm(getParam(3));

            if (name == null)
                throw new JIPInstantiationException(2);

            if (!(name instanceof Expression) && !(name instanceof Atom) && !(name.unifiable(ConsList.NIL)))
                throw new JIPTypeException(JIPTypeException.ATOMIC, name);


//            if(name == arity)
//                return false;

            ConsCell newFunc;
            try {
                if (name.unifiable(ConsList.NIL)) {
                    return getParam(1).unify(name, varsTbl) && getParam(3).unify(Expression.createNumber(0), varsTbl);
                }

                if (arity == null)
                    throw new JIPInstantiationException(3);

                if (!(arity instanceof Expression))
                    throw new JIPTypeException(JIPTypeException.INTEGER, arity);

                if (!((Expression) arity).isInteger())
                    throw new JIPTypeException(JIPTypeException.INTEGER, arity);

                if (((Expression) arity).getValue() < 0)
                    throw new JIPDomainException("not_less_than_zero", arity);

                // if arity == 0 generate an atom
                if (((Expression) arity).getValue() == 0) {
                    return getParam(1).unify(name, varsTbl);
                }

                if (!(name instanceof Atom))
                    throw new JIPTypeException(JIPTypeException.ATOM, name);

                if (((Atom) name).getName().equals(".")) {
                    if ((int) ((Expression) arity).getValue() == 2) {
                        newFunc = new ConsList(new Variable(false), new Variable(false));
                        return newFunc.unify(getParam(1), varsTbl);
                    }
                }

                // generate new variables
                ConsCell params = null;

                for (int i = 0; i < (int) ((Expression) arity).getValue(); i++) {
                    params = new ConsCell(new Variable(false), params);
                }

                // generate new functor
                String strFuncName = new StringBuilder(((Atom) name).getName()).append("/").append((int) ((Expression) arity).getValue()).toString();
                // Check if BuiltIn
                if (BuiltInFactory.isBuiltIn(strFuncName)) {
                    newFunc = new BuiltInPredicate(strFuncName, params);
                } else {
                    newFunc = new Functor(strFuncName, params);
                }

                return newFunc.unify(getParam(1), varsTbl);
            } catch (NullPointerException ex) {
                throw new JIPInstantiationException();
            } catch (ClassCastException ex) {
                throw new JIPTypeException(JIPTypeException.FUNCTOR, func);
            }
        } else {
            PrologObject funcName;
            Expression funcArity;
            if (func instanceof ConsList) {
                if (func.unifiable(ConsList.NIL)) {
                    funcName = ConsList.NIL;
                    funcArity = Expression.createNumber(0);
                } else {
                    funcName = Atom.createAtom(".");
                    funcArity = Expression.createNumber(2);
                }
            } else if (func instanceof Functor) {
//                if(((Functor)func).getAtom().equals(Atom.COLON))
//                {
//                    try
//                    {
//                        func =  (Functor)((ConsCell)((Functor)func).getParams().getTail()).getHead();
//                    }
//                    catch(ClassCastException ex)
//                    {
//                        throw new JIPTypeException(JIPTypeException.FUNCTOR, func);
//                    }
//                }

                funcName = Atom.createAtom(((Functor) func).getFriendlyName());
                funcArity = Expression.createNumber(((Functor) func).getArity());
            } else if (func instanceof Atom) {
                funcName = (Atom) func;
                funcArity = Expression.createNumber(0);
            } else if (func instanceof Expression) {
//                funcName  = Atom.createAtom(Integer.toString((int)((Expression)func).getValue()));
                funcName = func;
                funcArity = Expression.createNumber(0);
            } else if (func instanceof ConsCell) {
                funcName = Atom.createAtom(",");
                funcArity = Expression.createNumber(2);
            } else
                throw new JIPTypeException(JIPTypeException.UNDEFINED, func);

            return new ConsCell(funcName, new ConsCell(funcArity, null)).unify(
                    new ConsCell(getParam(2), new ConsCell(getParam(3), null)), varsTbl);
        }
    }
}







