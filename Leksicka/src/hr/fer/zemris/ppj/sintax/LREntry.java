package hr.fer.zemris.ppj.sintax;

import java.util.Set;
import java.util.TreeSet;

import hr.fer.zemris.ppj.sintax.grammar.Production;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;

/**
 * @author fhrenic
 */
public class LREntry {

    private Production production;
    private int dotIndex;
    private Set<Symbol> followUps;

    public LREntry(Production production, Set<Symbol> followUps) {
        this.production = production;
        dotIndex = 0;
        this.followUps = followUps;
    }

    private LREntry(LREntry e) {
        this.production = e.production;
        this.dotIndex = e.dotIndex + 1;
        this.followUps = new TreeSet<>(e.followUps);
    }

    public int getDotIndex() {
        return dotIndex;
    }

    public LREntry next() {
        return new LREntry(this);
    }

    public boolean isEmptyAfterTransition() {
        return dotIndex >= production.emptyFrom();
    }

    public Symbol getTransitionSymbol() {
        return production.getRHS().get(dotIndex);
    }

    public boolean isComplete() {
        return dotIndex == production.getRHS().size();
    }

    @Override
    public int hashCode() {
        return production.hashCode() << 16 + dotIndex;
    }

    /**
     * @param pFollowUps
     */
    public void addFollowUps(Set<Symbol> followUps) {
        this.followUps.addAll(followUps);
    }

}
