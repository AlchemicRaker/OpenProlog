/*
 * 23/04/2014
 *
 * Copyright (C) 1999-2014 Ugo Chirico
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

final class Compare3 extends BuiltIn {
    //	Order is neither a variable nor an atom - type_error(atom, Order)
    public final boolean unify(final Hashtable<Variable, Variable> varsTbl) {
        final PrologObject order = getRealTerm(getParam(1));
        final PrologObject term1 = getParam(2);
        final PrologObject term2 = getParam(3);

        if (order == null) {
            PrologObject oper1;
            if (term1.lessThen(term2))
                oper1 = Atom.createAtom("<");
            else if (term2.lessThen(term1))
                oper1 = Atom.createAtom(">");
            else //if(term1.termEquals(term2))
                oper1 = Atom.createAtom("=");
//            else

            return getParam(1).unify(oper1, varsTbl);
        } else {
            if (!(order instanceof Atom))
                throw new JIPTypeException(JIPTypeException.ATOM, order);

            final String operator = ((Atom) order).getName();
            if (operator.length() > 1)
                throw new JIPDomainException("order", order);

            switch (operator.charAt(0)) {
                case '<':
                    return term1.lessThen(term2);

                case '>':
                    return term2.lessThen(term1);

                case '=':
                    return term1.termEquals(term2); // && !term2.lessThen(term1);

                default:
                    throw new JIPDomainException("order", order);
            }
        }
    }
}
