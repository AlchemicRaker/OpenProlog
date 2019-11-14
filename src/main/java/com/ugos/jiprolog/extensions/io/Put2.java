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
import java.io.OutputStream;
import java.util.Hashtable;

public class Put2 extends JIPXCall {
    public final boolean unify(final JIPCons params, Hashtable varsTbl) {
        JIPTerm output = params.getNth(1);
        JIPTerm c = params.getNth(2);

        // check if input is a variable
        if (output instanceof JIPVariable) {
            // try to extract the term
            if (!((JIPVariable) output).isBounded()) {
                throw new JIPInstantiationException(1);
            } else {
                //extracts the term
                output = ((JIPVariable) output).getValue();
            }
        }

        // check if input is a variable
        if (c instanceof JIPVariable) {
            // try to extract the term
            if (!((JIPVariable) c).isBounded()) {
                throw new JIPInstantiationException(1);
            } else {
                //extracts the term
                c = ((JIPVariable) c).getValue();
            }
        }

        char c1;
        if (c instanceof JIPAtom) {
            if (((JIPAtom) c).getName().length() > 1)
                throw new JIPTypeException(JIPTypeException.CHARACTER, c);

            c1 = ((JIPAtom) c).getName().charAt(0);
        } else if (c instanceof JIPNumber) {
            c1 = (char) ((JIPNumber) c).getDoubleValue();
            if (c1 == -1)  // ignore -1
                return true;

            if (c1 < -1 || c1 > Short.MAX_VALUE)  // ignore all
                throw new JIPTypeException(JIPTypeException.INTEGER, c);
        } else {
            throw new JIPTypeException(JIPTypeException.INTEGER, c);
        }

        OutputStream writer;

        StreamInfo sinfo = JIPio.getStreamInfo(output);

        String mode = sinfo.getProperties().getProperty("mode");
        if (!(mode.equals("mode(write)") || mode.equals("mode(append)")))
            throw new JIPPermissionException("output", "stream", sinfo.getAlias());

        // Get the stream
        writer = JIPio.getOutputStream(sinfo.getHandle(), getJIPEngine());


        try {
            writer.write(c1);
            writer.flush();
        } catch (IOException ex) {
            throw new JIPRuntimeException(JIPio.ERR_IOEXCEPTION, ex.getMessage());
        }

        return true;
    }

    public boolean hasMoreChoicePoints() {
        return false;
    }
}

