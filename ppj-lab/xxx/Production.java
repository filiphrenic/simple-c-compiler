package hr.fer.zemris.ppj.syntax.grammar;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * This class models productions in a grammar.
 * 
 * @author fhrenic
 * @author marko1597
 */
public class Production implements Comparable<Production>, Serializable {

    private static final long serialVersionUID = -1638197559527421240L;

    private Symbol lhs; // left hand side
    private Symbol[] rhs; // right hand side;
    private BitSet[] starts;

    private int id;
    private int emptyFrom; // index of first symbol after which there are all empty symbols
    private boolean isEpsilon;

    /**
     * Creates a new production from left hand side, right hand side and
     * production id.
     * 
     * @param lhs left hand side
     * @param rhs right hand side
     * @param id id number
     */
    public Production(Symbol lhs, List<Symbol> rhs, int id) {
        int n = rhs.size();
        this.id = id;
        this.lhs = lhs;
        this.rhs = new Symbol[n];
        rhs.toArray(this.rhs);
        starts = new BitSet[n];
        emptyFrom = 0;
        isEpsilon = n == 1 && rhs.get(0).equals(Symbol.EPS_SYMBOL);
    }

    /**
     * Finds a position from which the production is empty.
     */
    public void setEmptyFrom() {
        for (int idx = 0; idx < rhs.length; idx++) {
            if (!rhs[idx].isEmpty()) {
                emptyFrom = idx + 1;
            }
        }
    }

    /**
     * Caches start sets to this production so it doesn't need to be computed
     * every time.
     * 
     * @param globalStarts start sets that were computed for symbols
     * @param mapper mapper from symbol to it's alias integer
     */
    public void annotate(BitSet[] globalStarts, Map<Symbol, Integer> mapper) {
        if (isEpsilon) {
            return;
        }

        boolean lastWasEmpty = false;
        for (int i = rhs.length - 1; i >= 0; i--) {
            int idx = mapper.get(rhs[i]);
            starts[i] = (BitSet) globalStarts[idx].clone();
            if (lastWasEmpty) {
                starts[i].or(starts[i + 1]);
            }
            if (rhs[i].isEmpty()) {
                lastWasEmpty = true;
            }
        }
    }

    /**
     * @return left hand side symbol
     */
    public Symbol getLHS() {
        return lhs;
    }

    /**
     * @return size of right hand side
     */
    public int getSize() {
        return isEpsilon ? 0 : rhs.length;
    }

    /**
     * @param idx index
     * @return symbol at index position
     */
    public Symbol getAt(int idx) {
        return rhs[idx];
    }

    /**
     * Returns true if production is an epsilon production.
     * 
     * @return <code>true</code> if it's an epsilon production
     */
    public boolean isEpsilonProduction() {
        return isEpsilon;
    }

    /**
     * @return index from which this production is empty
     */
    public int emptyFrom() {
        return emptyFrom;
    }

    /**
     * @param idx index
     * @return start set at given position
     */
    public BitSet getStartsAtPosition(int idx) {
        return starts[idx];
    }

    /**
     * @return if this production is empty (if all symbols on the right hand
     *         side are empty)
     */
    public boolean isEmpty() {
        for (Symbol s : rhs) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Production o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        }
        return id == ((Production) obj).id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lhs);
        sb.append(" -> ");
        for (Symbol s : rhs) {
            sb.append(s);
        }
        return sb.toString();
    }

}
