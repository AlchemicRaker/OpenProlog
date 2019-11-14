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

import java.util.Enumeration;
import java.util.Hashtable;

final class Debugging0 extends Write1 {
    public final boolean unify(final Hashtable varsTbl) {
        Hashtable spyTable = (Hashtable) getJIPEngine().getEnvVariable("__spy__");
        if (spyTable != null) {
            Enumeration en = spyTable.elements();
            while (en.hasMoreElements()) {
                print(en.nextElement() + ",");
                //getJIPEngine().getCurrentOutputStream().print(enum.nextElement() + ",");
            }
        }

        return true;
    }
}
