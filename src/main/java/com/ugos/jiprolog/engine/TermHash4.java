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

final class TermHash4 extends BuiltIn {
    public final boolean unify(final Hashtable<Variable, Variable> varsTbl) {
        PrologObject term = getParam(1);
        PrologObject depth = getRealTerm(getParam(2));
        PrologObject range = getRealTerm(getParam(3));

        if (depth == null)
            throw new JIPInstantiationException();

        if (!(depth instanceof Expression))
            throw new JIPTypeException(JIPTypeException.EVALUABLE, depth);

        if (!((Expression) range).isInteger())
            throw new JIPTypeException(JIPTypeException.INTEGER, depth);

        if (range == null)
            throw new JIPInstantiationException();

        if (!(range instanceof Expression))
            throw new JIPTypeException(JIPTypeException.EVALUABLE, range);

        if (!((Expression) range).isInteger())
            throw new JIPTypeException(JIPTypeException.INTEGER, range);

        int d = (int) ((Expression) depth).getValue();
        int r = (int) ((Expression) range).getValue();

        Expression hash;

        if (!(Ground1.checkVariable(term))) {
            return true;
        } else if (d == 0) {
            hash = Expression.createNumber(("".hashCode() & 0xFFFFFFL) % r);
        } else {

            PrologObject term1 = getRealTerm(term);
            if (term1 == null) {
                hash = Expression.createNumber((term.toString().hashCode() & 0x7FFFFFFF) % r);
            } else {
                hash = Expression.createNumber((term1.toString().hashCode() & 0x7FFFFFFF) % r);
            }
        }

        return hash.unify((PrologObject) getParam(2), varsTbl);
    }
}
