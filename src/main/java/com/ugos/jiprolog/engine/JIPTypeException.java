/**
 *
 */
package com.ugos.jiprolog.engine;

/**
 * @author UgoChirico
 *
 *A type error occurs when the type of an argument or one of its components is not the expected type (but not a variable). ErrorTerm has the following form: type_error(Type, Culprit) where Type is the expected type and Culprit the argument which caused the error. Type is one of:

atom
atomic
boolean
byte
callable
character
compound
evaluable
fd_bool_evaluable
fd_evaluable
fd_variable
float
in_byte
in_character
integer
list
number
pair
predicate_indicator
 */
public class JIPTypeException extends JIPRuntimeException {

    /**
     *
     */

    public static final int UNDEFINED = 0;
    public static final int FUNCTOR = 1;
    public static final int ATOM = 2;
    public static final int ATOM_OR_STRING = 3;
    public static final int INTEGER = 4;
    public static final int FLOAT = 5;
    public static final int PREDICATE_INDICATOR = 6;
    public static final int COMPOUND = 7;
    public static final int LIST = 8;
    public static final int COMPARATION_OPERATOR = 9;
    public static final int CALLABLE = 10;
    public static final int EVALUABLE = 11;
    public static final int LIBRARY = 12;
    public static final int URL = 13;
    public static final int FILE = 14;
    public static final int ATOMIC = 15;
    public static final int CHARACTER = 16;
    public static final int NUMBER = 17;
    public static final int BYTE = 18;
    public static final int IN_BYTE = 19;
    public static final int IN_CHARACTER = 20;
    public static final int PAIR = 21;

    private static final String s_stringMap[] = new String[22];

    static {
        s_stringMap[FUNCTOR] = "functor";
        s_stringMap[ATOM] = "atom";
        s_stringMap[FLOAT] = "float";
        s_stringMap[ATOM_OR_STRING] = "atom_or_string";
        s_stringMap[INTEGER] = "integer";
        s_stringMap[PREDICATE_INDICATOR] = "predicate_indicator";
        s_stringMap[COMPOUND] = "compound";
        s_stringMap[LIST] = "list";
        s_stringMap[UNDEFINED] = "undefined";
        s_stringMap[COMPARATION_OPERATOR] = "comparation_operator";
        s_stringMap[EVALUABLE] = "evaluable";
        s_stringMap[CALLABLE] = "callable";
        s_stringMap[LIBRARY] = "library";
        s_stringMap[URL] = "url";
        s_stringMap[FILE] = "file";
        s_stringMap[ATOMIC] = "atomic";
        s_stringMap[CHARACTER] = "character";
        s_stringMap[NUMBER] = "number";
        s_stringMap[BYTE] = "byte";
        s_stringMap[IN_BYTE] = "in_byte";
        s_stringMap[IN_CHARACTER] = "in_character";
        s_stringMap[PAIR] = "pair";
    }

    private String typeError;
    private PrologObject culprit;

    public JIPTypeException(int typeError, JIPTerm culprit) {
        this(typeError, culprit.getRealTerm());
    }

    JIPTypeException(int typeError, PrologObject culprit) {
        if (typeError < UNDEFINED && typeError > CALLABLE)
            this.typeError = s_stringMap[UNDEFINED];
        else
            this.typeError = s_stringMap[typeError];

        this.culprit = culprit;
    }

    JIPTypeException(int typeError, PrologObject culprit, JIPEngine engine) {
        this(typeError, culprit);
        m_engine = engine;
    }

    @Override
    public JIPTerm getTerm() {
        return getTerm(new Functor("type_error/2", new ConsCell(Atom.createAtom(typeError), new ConsCell(culprit, null))));

//    	return getTerm("type_error(" + typeError + ", " + culprit + ")", strTerm);
    }

}
