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

import java.util.Hashtable;

final class Extern3 extends BuiltIn {
    public final boolean unify(final Hashtable varsTbl) {
        String strFunctName;
        String strModuleName;
        String strXClassName;
        String strAttributes;
        int nArity;
        ConsCell params;
        PrologObject pred = getRealTerm(getParam(1));

        // controlla se identificativo di modulo
        if (pred instanceof Functor && ((Functor) pred).getAtom().equals(Atom.COLON)) {
            params = ((Functor) pred).getParams();
            strModuleName = ((Atom) params.getHead()).getName();
            pred = ((ConsCell) params.getTail()).getHead();
        } else {
            strModuleName = getWAM().m_curNode.m_strModule;
        }

        // head deve essere instanza di funtore /2 del tipo name/arity
        if (pred instanceof Functor && ((Functor) pred).getAtom().equals(Atom.SLASHSLASH)) {
            params = ((Functor) pred).getParams();
            strFunctName = ((Atom) params.getHead()).getName();
            nArity = (int) ((Expression) ((ConsCell) params.getTail()).getHead()).getValue();
        } else {
            throw new JIPTypeException(JIPTypeException.PREDICATE_INDICATOR, pred);
        }

        final PrologObject exClass = getRealTerm(getParam(2));

        if (exClass instanceof PString)
            strXClassName = ((PString) exClass).getString();
        else if (exClass instanceof Atom)
            strXClassName = ((Atom) exClass).getName();
        else
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, exClass);

        final PrologObject attribs = getRealTerm(getParam(3));
        if (attribs instanceof PString)
            strAttributes = ((PString) attribs).getString();
        else if (attribs instanceof Atom)
            strAttributes = ((Atom) attribs).getName();
        else
            throw new JIPTypeException(JIPTypeException.ATOM_OR_STRING, attribs);

        apply(strFunctName, nArity, strModuleName, strXClassName, strAttributes);

        return true;
    }

    protected final void apply(String strFunctName, int nArity, String strModuleName, String strXClassName, String strAttributes) {
        try {
            JIPClausesDatabase jipDB;
            if (JIPEngine.getClassLoader() != null)
                jipDB = (JIPClausesDatabase) JIPEngine.getClassLoader().loadClass(strXClassName).newInstance();
            else
                jipDB = (JIPClausesDatabase) Class.forName(strXClassName).newInstance();


            jipDB.setFunctor(strFunctName, nArity);

            if (!strAttributes.isEmpty() && (strAttributes.charAt(0) == 39 || strAttributes.charAt(0) == 34)) {
                strAttributes = strAttributes.substring(1, strAttributes.length() - 1);
            }

            jipDB.setJIPEngine(getJIPEngine());
            jipDB.setAttributes(strAttributes);

            getJIPEngine().getGlobalDB().addClausesDatabase(jipDB, strModuleName, new StringBuilder(strFunctName).append("/").append(nArity).toString());
        } catch (ClassNotFoundException ex) {
            throw JIPExistenceException.createProcedureException(Atom.createAtom(strXClassName));
        } catch (IllegalAccessException ex) {
            throw JIPExistenceException.createProcedureException(Atom.createAtom(strXClassName));
//            throw JIPRuntimeException.create(38, strXClassName);
        } catch (InstantiationException ex) {
            throw JIPExistenceException.createProcedureException(Atom.createAtom(strXClassName));
//            throw JIPRuntimeException.create(39, strXClassName);
        } catch (ClassCastException ex) {
            throw JIPExistenceException.createProcedureException(Atom.createAtom(strXClassName));
//            throw JIPRuntimeException.create(40, strXClassName);
        }
    }
}
