package hr.fer.zemris.ppj.sintax;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Symbol {

    private SymbolType type;
    String symbol; 
    boolean printable;
    private static enum SymbolType {
        Terminal, NonTerminal, Sync;
    }
    public Symbol(SymbolType type, String sym, boolean printable){
    	this(type, sym);
    	this.printable = printable;
    }
    public Symbol(SymbolType type, String sym){
    	this(type);
    	this.symbol = sym;
    	this.printable = true;
    }
	public Symbol(SymbolType type) {
		super();
		this.type = type;
	}
	@Override
	public String toString(){
		return symbol;
	}
	public SymbolType getType() {
		return type;
	}
}
