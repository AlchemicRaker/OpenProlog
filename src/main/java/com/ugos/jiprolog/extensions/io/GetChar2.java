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

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

public final class GetChar2 extends JIPXCall {
    private int streamHandle;

    protected final int readNextChar(InputStream ins) {
        if (streamHandle == JIPEngine.USER_INPUT_HANDLE)
            getJIPEngine().notifyEvent(JIPEvent.ID_WAITFORUSERINPUT, getPredicate(), getQueryHandle());

        try {
            return ins.read();
        } catch (IOException ex) {
            throw new JIPJVMException(ex);
        } finally {
            if (streamHandle == JIPEngine.USER_INPUT_HANDLE)
                getJIPEngine().notifyEvent(JIPEvent.ID_USERINPUTDONE, getPredicate(), getQueryHandle());
        }
    }

    public final boolean unify(final JIPCons params, Hashtable varsTbl) {
        // get first parameter
        JIPTerm input = params.getNth(1);
        JIPTerm ch = params.getNth(2);

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

        // Get the stream
        StreamInfo sinfo = JIPio.getInputStreamInfo(input, false);

        Properties properties = sinfo.getProperties();
//        if(!(properties.getProperty("mode").equals("mode(read)")))
//        	throw new JIPPermissionException("input", "stream", sinfo.getAlias());
//        if(!properties.getProperty("type").equals("type(text)"))
//        	throw new JIPPermissionException("input", "binary_stream", sinfo.getAlias());

        final InputStream ins = JIPio.getInputStream(streamHandle = sinfo.getHandle(), getJIPEngine());

        int c = -1;

        if (ch instanceof JIPVariable && ((JIPVariable) ch).isBounded()) {
            ch = ((JIPVariable) ch).getValue();
            if (!(ch instanceof JIPAtom))
                throw new JIPTypeException(JIPTypeException.IN_CHARACTER, ch);
            else if (((JIPAtom) ch).getName().equals("end_of_file"))
                ch = JIPAtom.create(String.valueOf((char) -1));
            else if (((JIPAtom) ch).getName().length() > 1)
                throw new JIPTypeException(JIPTypeException.IN_CHARACTER, ch);
        }

        if (properties.getProperty("end_of_stream").equals("end_of_stream(past)")) {
            if (properties.getProperty("eof_action").equals("eof_action(error)"))
                throw new JIPPermissionException("input", "past_end_of_stream", sinfo.getAlias());
            else if (properties.getProperty("eof_action").equals("eof_action(eof_code)"))
                return params.getNth(2).unify(JIPAtom.create("end_of_file"), varsTbl);
            else // eof_action(reset)
                c = readNextChar(ins);
        } else if (properties.getProperty("end_of_stream").equals("end_of_stream(at)")) {
            sinfo.setEndOfStream("past");
            return params.getNth(2).unify(JIPAtom.create("end_of_file"), varsTbl);
        } else {
            // end_of_stream(not)
            c = readNextChar(ins);
        }

        if (c == -1) {
            sinfo.setEndOfStream("past");
            return params.getNth(2).unify(JIPAtom.create("end_of_file"), varsTbl);
        } else if (c == 0) {
            throw new JIPRepresentationException("character");
        }

        JIPTerm term = JIPAtom.create(String.valueOf((char) c));

        return params.getNth(2).unify(term, varsTbl);
    }

    public boolean hasMoreChoicePoints() {
        return false;
    }
}
