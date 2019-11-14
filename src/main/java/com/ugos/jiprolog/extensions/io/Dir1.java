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

import java.io.File;
import java.util.Hashtable;

public class Dir1 extends JIPXCall {
    public final boolean unify(final JIPCons params, Hashtable<JIPVariable, JIPVariable> varsTbl) {
        File file = new File(getJIPEngine().getSearchPath());
        String files[] = file.list();
        JIPList fileList = JIPList.create(JIPAtom.create("."), null);
        fileList = JIPList.create(JIPAtom.create(".."), fileList);

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String strFileName;
                File cFile = new File(files[i]);
                if (cFile.isDirectory())
                    strFileName = "[" + files[i] + "]";
                else
                    strFileName = files[i];

                fileList = JIPList.create(JIPAtom.create(strFileName), fileList);
            }
        }


        fileList = fileList.reverse();

        return params.getNth(1).unify(fileList, varsTbl);
    }

    public boolean hasMoreChoicePoints() {
        return false;
    }
}

