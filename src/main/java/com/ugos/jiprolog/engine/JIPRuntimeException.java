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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;

/**
 * JIPRuntimeException is the base class af all exceptions raised by JIProlog.<br>
 * All runtime errors and exceptions are translated in JIPRuntimeException except JIPSyntaxErrorException.
 *
 * @author Ugo Chirico 2005<br>
 * Home Page: http://www.ugochirico.com
 * @version 3.0
 * @see com.ugos.jiprolog.engine.JIPEngine
 */
public class JIPRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 3046239634882549653L;

    protected JIPEngine m_engine;
    protected WAM.Node m_curNode;
    protected String m_strFileName;
    protected PrologObject m_term;

    protected int m_nLineNumber;
    protected int m_nPosition;

    public static int ID_USER_EXCEPTION;

    protected PrologObject exceptionTerm;

    public JIPRuntimeException(int errorNumber, String message) {
        super(getErrorMessage(errorNumber).replace("%1", message));
    }

    public JIPRuntimeException(PrologObject exceptionTerm) {
        this.exceptionTerm = exceptionTerm;
    }

    JIPRuntimeException(JIPRuntimeException ex) {
        super(ex.getMessage());
        m_curNode = ex.m_curNode;
    }

    /**
     * Constructs a new JIPRuntimeException with unknown error number
     *
     * @param strMsg message associated to this exception
     */
    public JIPRuntimeException(final String strMsg) {
        super(strMsg);
    }

    /**
     * Constructs an empty JIPRuntimeException
     */
    public JIPRuntimeException() {

    }

    /**
     * Gets the error term associated to this exception
     *
     * @return JIPTerm object associated to this exception
     */
    public JIPTerm getTerm() {
        if (exceptionTerm != null) {
            return getTerm(exceptionTerm);
        } else {
            String strMessage = super.getMessage();
            if (strMessage == null) strMessage = "undefined";

            strMessage = Atom.createAtom(strMessage).toString();

            return getTerm(new Functor("system_error/1", new ConsCell(Atom.createAtom(strMessage), null)));
        }
    }

    protected JIPTerm getTerm(final PrologObject type) {
        if (m_strFileName == null)
            m_strFileName = "undefined";

        PrologObject term = (m_term != null) ? m_term : ((m_curNode == null) ? Atom.createAtom("undefined") : m_curNode.getGoal());

        Functor fileFunctor = new Functor("file/2", new ConsCell(Atom.createAtom(m_strFileName), new ConsCell(Expression.createNumber(m_nLineNumber), null)));
        Functor contextFunctor = new Functor("context/2", new ConsCell(term, new ConsCell(fileFunctor, null)));
        Functor errorFunctor = new Functor("error/2", new ConsCell(type, new ConsCell(contextFunctor, null)));

        return JIPTerm.getJIPTerm(errorFunctor);
    }

    /**
     * Sets the error term associated to this exception
     *
     * @param term the term of this exception
     */
    public void setTerm(final PrologObject term) {
        m_term = term;//(m_curNode == null) ? null : new JIPTerm(m_curNode.getGoal().copy());
    }

    /**
     * Gets the file name where the exception was raised
     *
     * @return the file name where the exception was raised
     */
    public String getFileName() {
        return m_strFileName;
    }

    /**
     * Gets the line number where the exception was raised
     *
     * @return the line number where the exception was raised
     */
    public int getLineNumber() {
        return m_nLineNumber;
    }

    /**
     * Gets the position where the exception was raised
     *
     * @return the position where the exception was raised
     */
    public int getPosition() {
        return m_nPosition;
    }

    /**
     * Gets the error message
     *
     * @return the error message
     */
    public String getMessage() {
        if (getTerm() != null)
            return getTerm().toString();
        else
            return super.getMessage();
    }

    /**
     * Write the stack trace of goals to the default print stream
     */
    public void printPrologStackTrace() {
        printPrologStackTrace(System.out);
    }


    /**
     * Write the stack trace of goals to the given print stream
     *
     * @param ps print stream that receive the stack trace
     */
    public void printPrologStackTrace(PrintStream ps) {
        printPrologStackTrace(new PrintWriter(ps));
    }

    /**
     * Write the stack trace of goals to the given print writer
     *
     * @param pw printwriter that receive the stack trace
     */
    public void printPrologStackTrace(PrintWriter pw) {
        if (m_curNode == null) {
            pw.println("No stack trace available");
            pw.flush();
            return;
        }

        WAM.Node node = m_curNode;
        pw.println(node.getGoal().toString(m_engine));
        node = node.m_previous;

        while (node != null) {
            //System.out.println("node.getGoal() " + node.getGoal());
            if (node.getGoal() != null)
                pw.println(" - " + node.getGoal().toString(m_engine));
            pw.flush();
            node = node.m_previous;
        }
    }
    //#endif

    public static JIPRuntimeException createRuntimeException(int errorNumber) {
        return new JIPRuntimeException(getErrorMessage(errorNumber));
    }

    public static JIPRuntimeException createRuntimeException(int errorNumber, String message) {
        String error = getErrorMessage(errorNumber);
        error = error.replace("%1", message);

        return new JIPRuntimeException(error);
    }

    public static String getErrorMessage(int errorNumber) {
        String err = s_errorTable.get(errorNumber);
        return err != null ? err : "";
    }

    static final Hashtable<Integer, String> s_errorTable = new Hashtable<Integer, String>();

    static {
        s_errorTable.put(new Integer(-1), "Unknown Exception");
        s_errorTable.put(new Integer(0), "Execution aborted");
        s_errorTable.put(new Integer(1), "Syntax Error - %1");  // syntax error
        s_errorTable.put(new Integer(2), "Arithmetic Exception: %1");
        s_errorTable.put(new Integer(3), "One of the parameter is not of the expected type: %1");
        s_errorTable.put(new Integer(4), "One of the parameter is an unbounded variable: %1");
        s_errorTable.put(new Integer(5), "The predicate %1 has been defined in another file");
        s_errorTable.put(new Integer(6), "The file specified is not found: %1");
        s_errorTable.put(new Integer(7), "JVM Exception: %1");
        s_errorTable.put(new Integer(8), "JVM IOException: %1");
        s_errorTable.put(new Integer(9), "Security Exception. If JIPProlog is running as an applet it is subject to security limitations: %1");
        s_errorTable.put(new Integer(10), "Unable to add a clause to the related database");
        s_errorTable.put(new Integer(11), "The clause to add has an invalid head: %1");
        s_errorTable.put(new Integer(12), "Attempt to abolish a system predicate: %1");
        s_errorTable.put(new Integer(13), "Attempt to retract a system predicate: %1");
        s_errorTable.put(new Integer(14), "Attempt to redefine a system operator: %1");
        s_errorTable.put(new Integer(15), "Attempt to assert a system predicate: %1");
        s_errorTable.put(new Integer(16), "The precedence of the operator %1 is out of range (0, 1200)");
        s_errorTable.put(new Integer(17), "Invalid associativity specifier: %1");
        s_errorTable.put(new Integer(18), "The path specified is not found: %1");
        s_errorTable.put(new Integer(19), "Open snip <!/0 is not found");
        s_errorTable.put(new Integer(20), "Unable to write to the selected file");
        s_errorTable.put(new Integer(21), "Unexpected error found while consulting the file: %1");
        s_errorTable.put(new Integer(22), "The variable %1 is unbounded");
        s_errorTable.put(new Integer(23), "The meta-call variable %1 is unbounded");
        s_errorTable.put(new Integer(24), "Module directive defined twice in the file: %1");
        s_errorTable.put(new Integer(25), "The type of the term %1 is unknown");
        s_errorTable.put(new Integer(26), "Working dicectory has been set to null by the environment");
        s_errorTable.put(new Integer(27), "Directive fails while compiling the file: %1");
        s_errorTable.put(new Integer(28), "The file to load may be corrupted: %1");
        s_errorTable.put(new Integer(29), "Unexpected call");
        s_errorTable.put(new Integer(30), "Attempt to assert a static predicate: %1");
        /*        s_errorTable.put(new Integer(30), "The dialog specified as JIPDialog isn't modal: %1");
        s_errorTable.put(new Integer(31), "The JIPDialog class specified is not found:  %1");
        s_errorTable.put(new Integer(32), "The JIPDialog class does not implement JIPDialog interface correctly:  %1");
        s_errorTable.put(new Integer(33), "The JIPDialog class throws the following exception: %1");
        s_errorTable.put(new Integer(34), "The JIPDialog class specified cannot be accessed: %1");
        s_errorTable.put(new Integer(35), "The JIPDialog class specified cannot be instantiated: %1");
         s_errorTable.put(new Integer(36), "The class specified does not implement JIPDialog interface: %1");
         */
        s_errorTable.put(new Integer(37), "The JIPClausesDatabase class specified is not loaded: %1. Load it using load_library/1");
        s_errorTable.put(new Integer(38), "The JIPClausesDatabase class specified cannot be accessed: %1");
        s_errorTable.put(new Integer(39), "The JIPClausesDatabase class specified cannot be instantiated: %1");
        s_errorTable.put(new Integer(40), "The class specified does not implement JIPClausesDatabase interface:  %1");
        s_errorTable.put(new Integer(41), "The JIPXCall class specified is not loaded: %1. Load it using load_library/1");
        s_errorTable.put(new Integer(42), "The JIPXCall class specified cannot be accessed: %1");
        s_errorTable.put(new Integer(43), "The JIPXCall class specified cannot be instantiated: %1");
        s_errorTable.put(new Integer(44), "The class specified does not implement JIPXCall interface: %1");
        s_errorTable.put(new Integer(45), "Functor excepted instead of: %1");
        s_errorTable.put(new Integer(46), "Forbidden operation on a system predicate: %1");
        s_errorTable.put(new Integer(47), "Bad definition in module declaration: %1");
        s_errorTable.put(new Integer(48), "Operator %1 cannot be redefined");
        s_errorTable.put(new Integer(49), "There are no other solutions for the query: %1");

        s_errorTable.put(new Integer(100), "You cannot do this operation because the interpreter is still working");
        s_errorTable.put(new Integer(101), "You cannot do this operation because the query was closed");
        s_errorTable.put(new Integer(102), "The query specified in the handle was not found or it was closed");
        s_errorTable.put(new Integer(103), "The predicate %1 is undefined");
        s_errorTable.put(new Integer(104), "The predicate %1 is not supported");
        s_errorTable.put(new Integer(200), "Runtime Error");

        s_errorTable.put(new Integer(500), "ISO Exception");
    }
    /*
     Interpreter Generic Exceptions
     - 1, AbortException
     - 2, ArithmeticException
     - 3, JIPTypeException
     - 4, JIPParameterUnboundedException
     - 5, PredicateDefinedException
     - 6, FileNotFoundException
     - 7, Java Exception, JVM returns ex
     - 8, Java IOException, JVM returns ex
     - 9, "JIPProlog is running as an applet so it is subject to security limitation");

     Interpreter Exceptions
     10, "Functor " + m_strName + " has a wrong arity");
     11, "Can't retract clause " + m_currentRule.toString() + " from related database");
     12, "Unable to add a clause to the related database");
     13, "The clauses has an invalid head");
     14, "You are trying to redefine a static predicate");
     15, "You cannot use a clause as head of another clause");
     16, "Module name not defined");
     17, "Bad arity");
     18, "Try to abolish a static predicate");
     19, "Try to retract a static predicate");
     20, "Try to redefine a static operator");
     21, "Try to assert a static predicate");
     22, "Operators not associative"
     23, "Operator precedence out of range (0, 1200)");
     24, "Invalid associativity specifier");
     25, "Unexpected term found in file during compilation"
     26, "Path not found",
     27, "The path specified is bad");
     28, "The file parsed is too long. Please use extern/3 and link it as an external database of clauses");
     29, "Compiled prolog file not valid");
     30 - 44 xcall/3, dialog/3, extern/3 related errors
     45 - Close snip !]/0 not found
     46 - try to declare as multifile a static predicate
     47 - Attempt to close user stream
     48 - Unable to write to the selected path
     49 - An error is raised while consulting a file
     50 - The variable is unbounded
     51 - Meta-call variable unbounded
     52 - Unexpected terminal found in a grammar clause
     53 - free


     100   JIPIsRunningException
     101,  JIPQueryInExecutionException
     102,  BadQueryException
     103,  JIPInvalidHandleException
     104,  JIPQueryClosedException
     */
}
