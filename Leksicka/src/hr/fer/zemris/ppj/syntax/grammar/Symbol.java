package hr.fer.zemris.ppj.syntax.grammar;

import java.io.Serializable;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Symbol implements Comparable<Symbol>, Serializable {

    private static final long serialVersionUID = 2797783666592641402L;

    public static final String STREAM_END_NAME = "END";
    public static final String EPS_SYMBOL_NAME = "$";
    public static final String START_SYMBOL_NAME = "<s_crtano>";

    public static final Symbol EPS_SYMBOL;
    public static final Symbol START_SYMBOL;
    public static final Symbol STREAM_END;

    static {
        EPS_SYMBOL = new Symbol(SymbolType.TERMINAL, EPS_SYMBOL_NAME, true);
        EPS_SYMBOL.setEmpty(true);

        START_SYMBOL = new Symbol(SymbolType.NON_TERMINAL, START_SYMBOL_NAME, false);

        STREAM_END = new Symbol(SymbolType.TERMINAL, STREAM_END_NAME, false);
    }

    private String name;
    private SymbolType type;
    private boolean empty;
    private boolean printable;
    private boolean sync;

    public Symbol(SymbolType type, String name, boolean isPrintable) {
        this.type = type;
        this.name = name;
        this.empty = false;
        this.sync = false;
        this.printable = isPrintable;
    }

    public SymbolType getType() {
        return type;
    }

    /**
     * @return the isPrintable
     */
    public boolean isPrintable() {
        return printable;
    }

    /**
     * @param isPrintable
     */
    public void setPrintable(boolean isPrintable) {
        this.printable = isPrintable;
    }

    /**
     * @return the isEmpty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * @param empty
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
        if (type == o.type) {
            return name.compareTo(o.name);
        }
        if (type == SymbolType.NON_TERMINAL) {
            return -1;
        } else {
            return 1;
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
        return name.equals(s.name) && type == s.type;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + type.hashCode();
    }
}
