package hr.fer.zemris.ppj.sintax;

import java.util.List;

/**
 * @author fhrenic
 */
public class Grammar {

    private List<Symbol> terminalSymbols;
    private List<Symbol> nonTerminalSymbols;
    private List<Symbol> syncSymbols;
    private List<Production> productions;

    public List<Symbol> starts(Symbol sym) {
        // zapocinje znakom
        // zavrsni zavrsava jedino sam sa sobom
        // nezavrsni zavrsava s refleksivnim tranzitivnim okruzenjem...
        return null;
    }

    public List<LREntry> getEntries(Production p) {
        return null;
    }

}
