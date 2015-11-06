package hr.fer.zemris.ppj.sintax;

import java.util.List;

import hr.fer.zemris.ppj.sintax.grammar.Grammar;
import hr.fer.zemris.ppj.sintax.grammar.Production;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;

/**
 * @author fhrenic
 */
public class LREntry {

    private Production production;
    private int dotIndex;
    private List<Symbol> followSymbols;

    public LREntry(Grammar grammar, Production production) {
        this.production = production;
        dotIndex = 0;
        followSymbols = grammar.follows(production.getLeftHandSide());
    }

    private LREntry(LREntry e) {
        this.production = e.production;
        this.dotIndex = e.dotIndex + 1;
        this.followSymbols = e.followSymbols;
    }

    public LREntry next() {
        return new LREntry(this);
    }

    public Symbol getTransitionSymbol() {
        return production.getRightHandSide().get(dotIndex);
    }

    public boolean isComplete() {
        return dotIndex == production.getRightHandSide().size();
    }

    @Override
    public int hashCode() {
        return production.hashCode() << 16 + dotIndex;
    }

}
