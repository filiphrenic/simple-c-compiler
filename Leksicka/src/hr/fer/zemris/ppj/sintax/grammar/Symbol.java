package hr.fer.zemris.ppj.sintax.grammar;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Symbol {

    public static final Symbol STREAM_END = new Symbol(SymbolType.TERMINAL, "END", false);

    private SymbolType type;
    private String name;
    boolean printable;

    public Symbol(SymbolType type, String name, boolean printable) {
        this(type, name);
        this.printable = printable;
    }

    public Symbol(SymbolType type, String name) {
        this.type = type;
        this.name = name;
        this.printable = true;
    }

    @Override
    public String toString() {
        return name;
    }

    public SymbolType getType() {
        return type;
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
