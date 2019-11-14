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

public final class Read2 extends JIPXCall {
    private Enumeration m_termEnum;
    private boolean m_bEOF;
    private int m_streamHandle;

    private StreamInfo streamInfo;

    public final boolean unify(final JIPCons params, Hashtable varsTbl) {
        if (m_bEOF)
            return false;

        if (m_termEnum == null) // First time
        {
            // get first parameter
            JIPTerm input = params.getNth(1);

            // check if input is a variable
            if (input instanceof JIPVariable) {
                // try to extract the term
                if (!((JIPVariable) input).isBounded()) {
                    throw new JIPInstantiationException(1);
                } else {
                    //extracts the term
                    input = ((JIPVariable) input).getValue();
                }
            }

            streamInfo = JIPio.getInputStreamInfo(input, false);

            m_streamHandle = streamInfo.getHandle();
//            String mode = streamInfo.getProperties().getProperty("mode");
//            if(!(mode.equals("mode(read)")))
//            	throw new JIPPermissionException("input", "stream", streamInfo.getAlias());
//            if(!streamInfo.getProperties().getProperty("type").equals("type(text)"))
//            	throw new JIPPermissionException("input", "binary_stream", streamInfo.getAlias());

            // Get the stream
            m_termEnum = JIPio.getTermEnumeration(m_streamHandle, getJIPEngine());
            if (m_termEnum == null) {
                throw JIPExistenceException.createSourceSynkException(JIPNumber.create(m_streamHandle));
            }
        }

        boolean bUserStream;
        if (bUserStream = JIPEngine.USER_INPUT_HANDLE == m_streamHandle)
            getJIPEngine().notifyEvent(JIPEvent.ID_WAITFORUSERINPUT, getPredicate(), getQueryHandle());

        JIPTerm term;
        try {
            if (m_termEnum.hasMoreElements()) {
                term = (JIPTerm) m_termEnum.nextElement();

            } else {
                if (bUserStream) {
                    term = JIPCons.NIL;
                } else {
                    m_bEOF = true;
                    term = JIPAtom.create("end_of_file");
                    streamInfo.setEndOfStream("past");
                }
            }
        } catch (JIPRuntimeException ex) {
            if (bUserStream)
                getJIPEngine().notifyEvent(JIPEvent.ID_USERINPUTDONE, getPredicate(), getQueryHandle());

            throw ex;
        }

        if (bUserStream)
            getJIPEngine().notifyEvent(JIPEvent.ID_USERINPUTDONE, getPredicate(), getQueryHandle());

        // true if the term read unify with the second paramater
        return params.getNth(2).unify(term, varsTbl);
    }

    public boolean hasMoreChoicePoints() {
        return false;
    }
}

