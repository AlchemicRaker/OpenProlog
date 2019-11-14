/*
 * 23/04/2014
 *
 * Copyright (C) 1999-2014 Ugo Chirico
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

import com.ugos.jiprolog.engine.WAM.Node;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

class List extends ConsCell {
    final static long serialVersionUID = 300000006L;

    public final static List NIL = new List(null, null);

    public List(final PrologObject head, final PrologObject tail) {
        super(head, ((tail instanceof List || tail instanceof Functor) ? tail : ((tail instanceof ConsCell) ? new List((ConsCell) tail) : tail)));
    }

    public List(final ConsCell list) {
        this((list == null) ? null : list.getHead(), (list == null) ? null : list.getTail());
        //super(list.getHead(), list.getTail());
    }

    public boolean _unify(PrologObject obj, final Hashtable table) {
        if (obj instanceof Variable) {
            if (((Variable) obj).isBounded())
                obj = ((Variable) obj).getObject();
            else
                return ((Variable) obj)._unify(this, table);
        }

        if (obj instanceof List) {
//            System.out.println("*List Match: m_head " + m_head);
            if (m_head != null) {
                if (((ConsCell) obj).m_head == null)
                    return false;

                if (m_head._unify(((ConsCell) obj).m_head, table)) {
//                    System.out.println("*List Match: m_head match");
                    if (m_tail != null) {
                        return m_tail._unify(((ConsCell) obj).m_tail, table);
                    } else  // m_tail == null
                    {
                        if (((ConsCell) obj).m_tail instanceof Variable) {
                            return ((ConsCell) obj).m_tail._unify(m_tail, table);
                        } else if (((ConsCell) obj).m_tail == null) {
                            return true;
                        } else {
                            return ((ConsCell) obj).m_tail.unify(List.NIL, table);
                        }
                    }
                } else {
                    return false;
                }
            } else  //m_head == null
            {
                return (((ConsCell) obj).m_head == null);// ||   (((ConsCell)obj).m_head.match1(m_head, table)));
            }
        }
//        else if(obj instanceof Variable)
//        {
//            return obj._unify(this, table);
//        }
//        else if(obj instanceof Atom)
//        {
//        	return this == List.NIL && obj.equals(Atom.NIL;
//        }
        else if (obj == null) {
            return (m_head == null);
        } else
            return false;
    }

    public PrologObject copy(final boolean flat, final Hashtable<Variable, PrologObject> varTable) {
        if (m_head != null) {
            return new List(m_head.copy(flat, varTable),
                    (m_tail == null) ? null : m_tail.copy(flat, varTable));
        } else {
            return List.NIL;
        }
    }

    public final List reverse() {
        return new List(super.reverse());
    }

    public final ConsCell getConsCell() {
        if (this.isNil())
            return ConsCell.NIL;

        if (m_tail == null)
            return new ConsCell(m_head, null);
        else {
            List tail = ((List) BuiltIn.getRealTerm(m_tail));
            if (tail == null)
                return new ConsCell(m_head, tail);
            else
                return new ConsCell(m_head, ((List) BuiltIn.getRealTerm(m_tail)).getConsCell());
        }
    }

    public int member(PrologObject obj) {
        int size = getHeight();
        for (int i = 1; i <= size; i++) {
            if (getTerm(i).unifiable(obj))
                return i;
        }

        return 0;
    }

    @Override
    public Enumeration<PrologRule> getRulesEnumeration(Node curNode, WAM wam) {
        wam.moduleStack.push(curNode.m_strModule);

        // consult/1
        BuiltInPredicate term = new BuiltInPredicate("consult/1", new ConsCell(this, null));
        curNode.setGoal(term);
        return new RulesEnumerationBuiltIn(term, curNode.m_strModule, wam);
    }

    /**
     * Creates a new List
     *
     * @param termList vector of JIPTerm to transform in a prolog list
     * @return new JIPList object
     * @see com.ugos.jiprolog.engine.JIPTerm
     */
    public static final List create(final Collection<PrologObject> terms) {
        List list = null;

        for (PrologObject term : terms) {
            list = new List(term, list);
        }

        return list != null ? list.reverse() : NIL;
    }
}














