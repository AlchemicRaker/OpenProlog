/*
 * 09/19/2002
 *
 * Copyright (C) 1999-2003 Ugo Chirico
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

public final class FlushOutput1 extends JIPXCall {
    public final boolean unify(final JIPCons params, Hashtable varsTbl) {
        JIPTerm output = params.getNth(1);

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

        OutputStream writer;

        StreamInfo streamInfo = JIPio.getOutputStreamInfo(output, false);

//    	Properties props = streamInfo.getProperties();
//        if(!(props.getProperty("mode", "").equals("mode(append)")) &&
//           !(props.getProperty("mode", "").equals("mode(write)")))
//            throw new JIPPermissionException("output", "stream", streamInfo.getAlias());

        // Get the stream
        writer = JIPio.getOutputStream(streamInfo.getHandle(), getJIPEngine());

        try {
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

