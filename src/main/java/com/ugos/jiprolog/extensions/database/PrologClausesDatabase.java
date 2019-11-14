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

package com.ugos.jiprolog.extensions.database;

import com.ugos.jiprolog.engine.JIPClause;
import com.ugos.jiprolog.engine.JIPClausesDatabase;
import com.ugos.jiprolog.engine.JIPFunctor;
import com.ugos.jiprolog.engine.JIPRuntimeException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class PrologClausesDatabase extends JIPClausesDatabase {
    private PrintWriter m_writer;
    private String m_strFileName;

    public PrologClausesDatabase() {
        super();
    }

    public String getFileName() {
        return m_strFileName;
    }

    public void setAttributes(String strAttribs) {
        m_strFileName = strAttribs;
    }

    public boolean addClauseAtFirst(JIPClause clause) {
        return false;
    }

    public synchronized boolean addClause(JIPClause clause) {
        try {
            m_writer = new PrintWriter(new FileWriter(m_strFileName, true));
        } catch (IOException ex) {
            throw new JIPRuntimeException(JIPRuntimeException.ID_USER_EXCEPTION + 1, ex.toString());
        }


        m_writer.println(clause.getHead().toString() + ".");
        m_writer.flush();
        m_writer.close();

        return true;
    }

    public boolean removeClause(JIPClause clause) {
        return false;
    }

    public Enumeration clauses(JIPFunctor functor) {
        return new PrologClausesEnumeration(this);
    }

    public void finalize() {
        m_writer.close();
    }

    @Override
    public Enumeration clauses() {

        return clauses(null);
    }
}
