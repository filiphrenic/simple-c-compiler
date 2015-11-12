package hr.fer.zemris.ppj.sintax;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import hr.fer.zemris.ppj.sintax.grammar.Production;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;

/**
 * @author fhrenic
 */
public class LREntry implements Comparable<LREntry>, Serializable {

    private static final long serialVersionUID = 6695195208613158733L;

    private Production production;
    private int dotIndex;
    private Set<Symbol> startSet;

    public LREntry(Production production, Set<Symbol> startSet) {
        this.production = production;
        dotIndex = 0;
        this.startSet = startSet;
    }

    private LREntry(LREntry e) {
        this.production = e.production;
        this.dotIndex = e.dotIndex + 1;
        this.startSet = new TreeSet<>(e.startSet);
    }

    public Set<Symbol> getStartSet() {
        return startSet;
    }

    public LREntry next() {
        return new LREntry(this);
    }

    public boolean isComplete() {
        return production.isEpsilonProduction() || dotIndex == production.getRHS().size();
    }

    public boolean isEmptyAfterDot() {
        return dotIndex >= production.emptyFrom();
    }

    public Symbol getTransitionSymbol() {
        return production.getRHS().get(dotIndex);
    }

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
        return 31 * dotIndex + production.hashCode() << 16;
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

    public Iterable<Symbol> getSymbolsAfterDot() {
        return new Iterable<Symbol>() {
            @Override
            public Iterator<Symbol> iterator() {
                return new SymbolDotIt(dotIndex);
            }
        };
    }

    private class SymbolDotIt implements Iterator<Symbol> {
        private int idx;

        public SymbolDotIt(int idx) {
            this.idx = idx;
        }

        @Override
        public boolean hasNext() {
            return idx < production.getRHS().size();
        }

        @Override
        public Symbol next() {
            return production.getRHS().get(idx++);
        }

        @Override
        public void remove() {
        }

    }
}
