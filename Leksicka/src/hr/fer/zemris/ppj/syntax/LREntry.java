package hr.fer.zemris.ppj.syntax;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.fer.zemris.ppj.syntax.grammar.Production;
import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * This class represents a production with a dot. This is used to create tables
 * for the parser. Example A->aBC, creates entry A->[dot]aBC
 * 
 * @author fhrenic
 */
public class LREntry implements Comparable<LREntry>, Serializable {

    private static final long serialVersionUID = 6695195208613158733L;

    private Production production;
    private int dotIndex;
    private Set<Symbol> startSet;

    /**
     * Creates a new entry from all needed parameters. Dot is set at the front
     * of productions right side.
     * 
     * @param production production
     * @param startSet start set of this entry
     */
    public LREntry(Production production, Set<Symbol> startSet) {
        this.production = production;
        dotIndex = 0;
        this.startSet = startSet;
    }

    /**
     * Helper, copies the given entry and moves the dot one step.
     * 
     * @param e entry to copy
     */
    private LREntry(LREntry e) {
        this.production = e.production;
        this.dotIndex = e.dotIndex + 1;
        this.startSet = new HashSet<>(e.startSet);
    }

    /**
     * @return start set
     */
    public Set<Symbol> getStartSet() {
        return startSet;
    }

    /**
     * The next entry is the same as this entry but it has dot moved one towards
     * production end. A->a[dot]BC => A->aB[dot]C
     * 
     * @return next entry
     */
    public LREntry next() {
        return new LREntry(this);
    }

    /**
     * Returns if this entry is complete. It is complete if the dot is at the
     * end of the production. A->aBC[dot] is complete, A->[dot]aBC isn't.
     * C->[dot]$ is also complete because it is an epsilon production
     * 
     * @return <code>true</code> if entry is complete
     */
    public boolean isComplete() {
        return production.isEpsilonProduction() || dotIndex == production.getRHS().size();
    }

    /**
     * Returns <code>true</code> if the symbols after the dot are empty
     * 
     * @return <code>true</code> if symbols right of the dot are empty
     */
    public boolean isEmptyAfterDot() {
        return dotIndex >= production.emptyFrom();
    }

    /**
     * Returns the symbol that follows the dot
     * 
     * @return transition symbol
     */
    public Symbol getTransitionSymbol() {
        return production.getRHS().get(dotIndex);
    }

    /**
     * @return underlying production
     */
    public Production getProduction() {
        return production;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(production.getLHS());
        sb.append(" -> ");
        for (int idx = 0; idx < production.getRHS().size(); idx++) {
            if (idx == dotIndex) {
                sb.append('*');
            }
            sb.append(production.getRHS().get(idx));
        }
        if (production.getRHS().size() == dotIndex) {
            sb.append('*');
        }
        sb.append(", { ");
        for (Symbol s : startSet) {
            sb.append(s);
            sb.append(' ');
        }
        sb.append('}');

        return sb.toString();
    }

    @Override
    public int compareTo(LREntry o) {
        // will return the one that has higher priority
        // move/reduce -> move
        // reduce/reduce -> which production was defined first?

        boolean comp1 = isComplete();
        boolean comp2 = o.isComplete();

        if (comp1 ^ comp2) {
            return comp1 ? 1 : -1;
        }

        int c = production.compareTo(o.production);
        if (c != 0) {
            return c;
        }

        return Integer.compare(dotIndex, o.dotIndex);
    }

    @Override
    public int hashCode() {
        return 31 * production.hashCode() + dotIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LREntry)) {
            return false;
        }
        LREntry other = (LREntry) obj;
        return dotIndex == other.dotIndex && production.equals(other.production)
                && startSet.equals(other.startSet);
    }

    /**
     * @param startSets
     * @return
     */
    public Set<Symbol> getStartSetFromDot(Map<Symbol, Set<Symbol>> startSets) {
        List<Symbol> rhs = production.getRHS();
        Set<Symbol> startS = new HashSet<>(startSet);
        for (int idx = dotIndex; idx < rhs.size(); idx++) {
            Symbol s = rhs.get(idx);
            startS.addAll(startSets.get(s));
            if (!s.isEmpty()) {
                break;
            }
        }
        return startS;
    }
}
