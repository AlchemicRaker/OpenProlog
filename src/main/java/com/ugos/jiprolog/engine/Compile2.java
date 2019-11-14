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

import com.ugos.io.PushbackLineNumberInputStream;

import java.io.*;
import java.util.Hashtable;

//import com.ugos.util.MapperHolder;

final class Compile2 extends BuiltIn {
    public final boolean unify(final Hashtable<Variable, Variable> varsTbl) {
        String strPath = null;
        String strDestinationFolder = null;

        final PrologObject path = getRealTerm(getParam(1));
        final PrologObject destinationFolder = getRealTerm(getParam(2));

        if (path instanceof Atom)
            strPath = ((Atom) path).getName();
        else if (path instanceof PString)
            strPath = ((PString) path).getString();
        else
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, path);

        if (destinationFolder instanceof Atom)
            strDestinationFolder = ((Atom) destinationFolder).getName();
        else if (destinationFolder instanceof PString)
            strDestinationFolder = ((PString) destinationFolder).getString();
        else if (destinationFolder.unifiable(List.NIL))
            strDestinationFolder = null;
        else
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, path);

        compile(strPath, strDestinationFolder, getJIPEngine());

        return true;
    }

    public static final void compile(String strPath, String strDestinationFolder, final JIPEngine engine) {
        InputStream ins = null;
//        InputStream oldins = null;
//        int oldInputStreamName = 0;
        try {
            String strFileName[] = new String[1];
            String strCurDir[] = new String[1];
            ins = StreamManager.getStreamManager().getInputStream(strPath, engine.getSearchPath(), strFileName, strCurDir);
//            oldins = engine.getCurrentInputStream();
//            oldInputStreamName = engine.getCurrentInputStreamHandle();
//            engine.setCurrentInputStream(ins, strPath.hashCode());

            File outf;
            if (strDestinationFolder == null) {
                final int nPos = strFileName[0].lastIndexOf('.');
                strPath = new StringBuilder(strFileName[0].substring(0, nPos)).append(".jip").toString();

                outf = new File(strPath);
            } else {
                String file = new File(strPath).getName();
                final int nPos = file.lastIndexOf('.');
                file = new StringBuilder(file.substring(0, nPos)).append(".jip").toString();
                outf = new File(strDestinationFolder, file);
            }

            final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outf));

//            ArrayList<PrologObject> program = new ArrayList<PrologObject>();
            PrologParser parser = new PrologParser(new ParserReader(new PushbackLineNumberInputStream(ins)), engine.getOperatorManager(), engine, strPath);

            try {
                PrologObject term;

                while ((term = parser.parseNext()) != null) {
                    out.writeObject(term);
                }

                ins.close();

//                predList = (List)predList.reverse();
            } catch (IOException ex) {
                ins.close();
                out.close();

                ex.printStackTrace();
//                engine.setCurrentInputStream(oldins, oldInputStreamName);
                throw new JIPJVMException(ex);
            }

//          engine.setCurrentInputStream(ins, oldInputStreamName);

            ins.close();
            out.close();

        } catch (FileNotFoundException ex) {
            try {
                if (ins != null)
                    ins.close();
            } catch (IOException ex1) {
            }
            ;

//            if(oldins != null)
//                engine.setCurrentInputStream(oldins, oldInputStreamName);

            throw JIPExistenceException.createSourceSynkException(Atom.createAtom(strPath));
        } catch (IOException ex) {
//            if(oldins != null)
//                engine.setCurrentInputStream(oldins, oldInputStreamName);

            try {
                if (ins != null)
                    ins.close();
            } catch (IOException ex1) {
            }

            throw new JIPJVMException(ex);
        } catch (SecurityException ex) {
//            if(oldins != null)
//                engine.setCurrentInputStream(oldins, oldInputStreamName);

            try {
                if (ins != null)
                    ins.close();
            } catch (IOException ex1) {
            }

            throw new JIPPermissionException("access", "source_sink", Atom.createAtom(strPath));
        } catch (JIPRuntimeException ex) {
//            if(oldins != null)
//                engine.setCurrentInputStream(oldins, oldInputStreamName);

            ex.m_strFileName = strPath;

            try {
                if (ins != null)
                    ins.close();
            } catch (IOException ex1) {
            }

            throw ex;
        }
    }
}
