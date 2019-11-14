/*
 * Copyright (C) 1999-2004 By Ugo Chirico
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

package com.ugos.jiprolog.extensions.io;

import com.ugos.jiprolog.engine.*;

import java.util.Enumeration;
import java.util.Hashtable;

public final class CurrentStream1 extends JIPXCall {
    private Enumeration m_ienum = null;
    private Enumeration m_oenum = null;

    public final boolean unify(final JIPCons params, final Hashtable varsTbl) {
        JIPTerm handle = params.getNth(1).getValue();

        if (m_ienum == null) {
            m_ienum = JIPio.itable.elements();
        }

        if (m_oenum == null) {
            m_oenum = JIPio.otable.elements();
        }

        JIPTerm stream;


        while (m_ienum.hasMoreElements()) {
            StreamInfo sinfo = (StreamInfo) m_ienum.nextElement();

            if (handle instanceof JIPAtom) {
                stream = JIPAtom.create(sinfo.getAlias());
            } else if (handle == null || handle instanceof JIPNumber) {
                stream = JIPNumber.create(sinfo.getHandle());
            } else {
                throw new JIPDomainException("stream_or_alias", handle);
            }


            JIPCons cons = JIPCons.create(stream, null);

            if (params.unifiable(cons))
                return params.unify(cons, varsTbl);
        }

        while (m_oenum.hasMoreElements()) {
            StreamInfo sinfo = (StreamInfo) m_oenum.nextElement();

            if (handle instanceof JIPAtom) {
                stream = JIPAtom.create(sinfo.getAlias());
            } else if (handle == null || handle instanceof JIPNumber) {
                stream = JIPNumber.create(sinfo.getHandle());
            } else {
                throw new JIPDomainException("stream_or_alias", handle);
            }


            JIPCons cons = JIPCons.create(stream, null);

            if (params.unifiable(cons))
                return params.unify(cons, varsTbl);
        }

        return false;
    }

    public boolean hasMoreChoicePoints() {
        return m_ienum.hasMoreElements() || m_oenum.hasMoreElements();
    }
}

