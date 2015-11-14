package hr.fer.zemris.ppj.syntax;

import java.io.Serializable;
import java.util.BitSet;

import hr.fer.zemris.ppj.syntax.grammar.Production;
import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * This class represents a production with a dot. This is used to create tables
 * for the parser. Example A->aBC, creates entry A->[dot]aBC
 * 
 * @author fhrenic
 */
public class LREntry implements Comparable<LREntry>, Serializable {

    public static void main(String[] args) {
        BitSet x = new BitSet();
        BitSet y = new BitSet(100);
        System.out.println(x.size());
        System.out.println(y.size());

        System.out.println(equalBits(x, y));
    }

    private static final long serialVersionUID = 6695195208613158733L;

    private Production production;
    private int dotIndex;
    private BitSet startSet;

    /**
     * Creates a new entry from all needed parameters. Dot is set at the front
     * of productions right side.
     * 
     * @param production production
     * @param startSet start set of this entry
     */
    public LREntry(Production production, BitSet startSet) {
        this.production = production;
        dotIndex = 0;
        this.startSet = startSet;
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
     * Helper, copies the given entry and moves the dot one step.
     * 
     * @param e entry to copy
     */
    private LREntry(LREntry e) {
        this.production = e.production;
        this.dotIndex = e.dotIndex + 1;
        this.startSet = (BitSet) e.startSet.clone();
    }

    /**
     * Returns if this entry is complete. It is complete if the dot is at the
     * end of the production. A->aBC[dot] is complete, A->[dot]aBC isn't.
     * C->[dot]$ is also complete because it is an epsilon production
     * 
     * @return <code>true</code> if entry is complete
     */
    public boolean isComplete() {
        return production.isEpsilonProduction() || dotIndex == production.getSize();
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
     * @return underlying production
     */
    public Production getProduction() {
        return production;
    }

    /**
     * Returns the symbol that follows the dot
     * 
     * @return transition symbol
     */
    public Symbol getTransitionSymbol() {
        return production.getAt(dotIndex);
    }

    /**
     * @return start set
     */
    public BitSet getStartSet() {
        return startSet;
    }

    /**
     * Returns start set for symbols after dot.
     * 
     * @return start set
     */
    public BitSet getStartSetFromDot() {
        if (isComplete()) {
            return new BitSet();
        } else {
            return production.getStartsAtPosition(dotIndex);
        }
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
        int hash = production.hashCode();
        hash = hash * 31 + dotIndex;
        hash = hash * 31 + startSet.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LREntry)) {
            return false;
        }
        LREntry other = (LREntry) obj;
        return dotIndex == other.dotIndex && production.equals(other.production)
        //&& startSet.equals(other.startSet);
                && equalBits(startSet, other.startSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(production.getLHS());
        sb.append(" -> ");
        if (production.isEpsilonProduction()) {
            sb.append('*');
        } else {
            for (int idx = 0; idx < production.getSize(); idx++) {
                if (idx == dotIndex) {
                    sb.append('*');
                }
                sb.append(production.getAt(idx));
            }
            if (production.getSize() == dotIndex) {
                sb.append('*');
            }
        }
        sb.append(", ");
        sb.append(startSet);

        return sb.toString();
    }

    /**
     * Checks if two bitsets are equal.
     * 
     * @param s1 first bitset
     * @param s2 second bitset
     * @return <code>true</code> if they are equal
     */
    private static boolean equalBits(BitSet s1, BitSet s2) {
        int i = -1, j = -1;
        while (true) {
            i = s1.nextSetBit(i + 1);
            j = s2.nextSetBit(j + 1);
            if (i != j) {
                return false;
            }
            if (i == -1) {
                return j == -1;
            }
        }
    }

}
