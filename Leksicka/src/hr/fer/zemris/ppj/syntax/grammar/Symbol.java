package hr.fer.zemris.ppj.syntax.grammar;

import java.io.Serializable;

/**
 * Class to represent a single symbol in grammar (can be either terminal or non
 * terminal).
 * 
 * @author fhrenic
 * @author marko1597
 */
public class Symbol implements Comparable<Symbol>, Serializable {

    private static final long serialVersionUID = 2797783666592641402L;

    public static final String EPS_SYMBOL_NAME = "$";
    public static final String STREAM_END_NAME = "###";
    public static final String START_SYMBOL_NAME = "%%%";

    public static final Symbol EPS_SYMBOL;
    public static final Symbol STREAM_END;
    public static final Symbol START_SYMBOL;

    static {
        EPS_SYMBOL = new Symbol(EPS_SYMBOL_NAME, true);
        EPS_SYMBOL.setEmpty(true);
        STREAM_END = new Symbol(STREAM_END_NAME, true);
        START_SYMBOL = new Symbol(START_SYMBOL_NAME, false);
    }

    private String name;
    private boolean isTerminal;
    private boolean empty;
    private boolean sync;

    public Symbol(String name, boolean isTerminal) {
        this.name = name;
        this.isTerminal = isTerminal;
        this.empty = false;
        this.sync = false;
    }

    /**
     * @return the isTerminal
     */
    public boolean isTerminal() {
        return isTerminal;
    }

    /**
     * @return the empty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * @param empty the empty to set
     */
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    /**
     * @return the sync
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * @param sync the sync to set
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @Override
    public int compareTo(Symbol o) {
        if (isTerminal == o.isTerminal) {
            return name.compareTo(o.name);
        }
        if (isTerminal) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol)) {
            return false;
        }
        Symbol s = (Symbol) obj;
        return name.equals(s.name) && isTerminal == s.isTerminal;
    }

    @Override
    public int hashCode() {
        return (isTerminal ? 1 : 0) * 31 + name.hashCode();
    }
}
