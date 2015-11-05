package hr.fer.zemris.ppj.sintax;

/**
 * @author fhrenic
 */
public class Symbol {

    private SymbolType type;

    private static enum SymbolType {
        Terminal, NonTerminal, Sync;
    }

	public Symbol(SymbolType type) {
		super();
		this.type = type;
	}

}
