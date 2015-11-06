package hr.fer.zemris.ppj.sintax.grammar;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Symbol {

    public static final Symbol STREAM_END = new Symbol(SymbolType.TERMINAL, "END", false);

    private String name;
    private SymbolType type;
    boolean isEmpty;
    boolean isPrintable;

    public Symbol(SymbolType type, String name, boolean isPrintable) {
        this.type = type;
        this.name = name;
        this.isEmpty = false;
        this.isPrintable = isPrintable;
    }

    public SymbolType getType() {
        return type;
    }

    /**
     * @return the isPrintable
     */
    public boolean isPrintable() {
        return isPrintable;
    }

    /**
     * @param isPrintable
     */
    public void setPrintable(boolean isPrintable) {
        this.isPrintable = isPrintable;
    }

    /**
     * @return the isEmpty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * @param isEmpty
     */
    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
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
