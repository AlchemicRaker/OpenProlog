/*****************************************
 * 27/03/2003
 *
 * Copyright (C) 1999-2003 Ugo Chirico
 * http://www.ugochirico.com
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
 *****************************************/

package com.ugos.jiprolog.extensions.terms;

import com.ugos.jiprolog.engine.*;

import java.util.Hashtable;

public class Numbervars3 extends JIPXCall {
    public final boolean unify(final JIPCons input, Hashtable varsTbl) {
        JIPTerm term = input.getNth(1);
        JIPTerm start = input.getNth(2).getValue();
        JIPTerm end = input.getNth(3);

        // check if input is a variable
        if (start == null) {
            throw new JIPInstantiationException(1);
        } else if (!(start instanceof JIPNumber))
            throw new JIPTypeException(JIPTypeException.INTEGER, start);
        else if (!((JIPNumber) start).isInteger())
            throw new JIPTypeException(JIPTypeException.INTEGER, start);

        int nStart = (int) ((JIPNumber) start).getDoubleValue();

        // check if input is a variable
        if (term instanceof JIPVariable) {
            // try to extract the term
            if (!((JIPVariable) term).isBounded()) {
                term.unify(JIPFunctor.create("$VAR", JIPCons.create(JIPNumber.create(nStart), null)), varsTbl);
                return end.unify(JIPNumber.create(nStart + 1), varsTbl);
            } else {
                term = term.getValue();
            }
        }

        int nCount = 0;
        JIPVariable vars[] = term.getUnboundedVariables();

        for (int i = 0; i < vars.length; i++) {
            vars[i].unify(JIPFunctor.create("$VAR", JIPCons.create(JIPNumber.create(nCount + nStart), null)), varsTbl);
            nCount++;
        }

        return end.unify(JIPNumber.create(nStart + nCount), varsTbl);
    }

    public boolean hasMoreChoicePoints() {
        return false;
    }
}

