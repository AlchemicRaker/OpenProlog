/*
 * 28/03/2003
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


package com.ugos.jiprolog.extensions.reflect;

import com.ugos.jiprolog.engine.*;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class JIPGet3 extends JIPXCall {
    private JIPTerm m_retVal;

    public final boolean unify(final JIPCons params, Hashtable varsTbl) {
        JIPTerm handle = params.getNth(1);
        JIPTerm fieldName = params.getNth(2);
        JIPTerm retVal = params.getNth(3);

        // check if className is a variable
        if (handle instanceof JIPVariable) {
            // try to extract the term
            if (!((JIPVariable) handle).isBounded()) {
                throw new JIPInstantiationException(1);
            } else {
                //extracts the term
                handle = ((JIPVariable) handle).getValue();
            }
        }

        if (!(handle instanceof JIPAtom))
            throw new JIPRuntimeException(JIPxReflect.ERR_UNEXPECTED_TERM, JIPxReflect.STR_UNEXPECTED_TERM);

        // check if className is a variable
        if (fieldName instanceof JIPVariable) {
            // try to extract the term
            if (!((JIPVariable) fieldName).isBounded()) {
                throw new JIPInstantiationException(2);
            } else {
                //extracts the term
                fieldName = ((JIPVariable) fieldName).getValue();
            }
        }

        if (!(fieldName instanceof JIPAtom))
            throw new JIPRuntimeException(JIPxReflect.ERR_UNEXPECTED_TERM, JIPxReflect.STR_UNEXPECTED_TERM);

        try {
            Field field;
            Object objRetVal;

            String atomHandle = ((JIPAtom) handle).getName();

            if (atomHandle.startsWith("#")) {
                // get the object
                Object obj = JIPxReflect.getObject(atomHandle);
                if (obj == null)
                    throw new JIPRuntimeException(JIPxReflect.ERR_OBJECT_NOT_FOUND, JIPxReflect.STR_OBJECT_NOT_FOUND);

                field = obj.getClass().getField(((JIPAtom) fieldName).getName());

                // invoke method
                objRetVal = field.get(obj);
            } else {
                // get the class
                Class objClass = getClass().forName(atomHandle);

                // get the rigth constructor
                field = objClass.getField(((JIPAtom) fieldName).getName());

                // invoke method
                objRetVal = field.get(null);
            }

            // Marshall value
            JIPTerm retVal1 = JIPxReflect.marshallOut(objRetVal);

            return retVal.unify(retVal1, varsTbl);
        } catch (ClassCastException ex) {
            throw new JIPRuntimeException(JIPxReflect.ERR_CLASS_CAST, JIPxReflect.STR_CLASS_CAST);
        } catch (ClassNotFoundException ex) {
            throw new JIPRuntimeException(JIPxReflect.ERR_CLASS_NOT_FOUND, JIPxReflect.STR_CLASS_NOT_FOUND);
        } catch (NoSuchFieldException ex) {
            //ex.printStackTrace();
            throw new JIPRuntimeException(JIPxReflect.ERR_METHOD_NOT_FOUND, JIPxReflect.STR_METHOD_NOT_FOUND);
        } catch (IllegalAccessException ex) {
            throw new JIPRuntimeException(JIPxReflect.ERR_INSTANTIATION, ex.getMessage());
        }
    }

    public final boolean hasMoreChoicePoints() {
        return false;
    }
}

