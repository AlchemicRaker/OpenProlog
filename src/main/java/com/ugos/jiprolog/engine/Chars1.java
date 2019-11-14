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

final class Chars1 extends BuiltIn {
    public final boolean unify(final Hashtable<Variable, Variable> varsTbl) {
        final PrologObject term = getRealTerm(getParam(1));

        String double_quotes = (String) getJIPEngine().getEnvVariable("double_quotes");
        if ("atom".equals(double_quotes)) {
            return term instanceof Atom;
        } else if (term instanceof PString) {
            return true;
        } else if (term instanceof List) {
            try {
                new PString((List) term, "chars".equals(double_quotes));

                return true;
            } catch (JIPTypeException ex) {
//              System.out.println(ex);
            }
        }

        return false;
    }
}
