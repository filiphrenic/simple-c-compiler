package hr.fer.zemris.ppj.syntax.grammar;

import java.io.Serializable;
import java.util.List;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production implements Comparable<Production>, Serializable {

    private static final long serialVersionUID = -1638197559527421240L;

    private int id;
    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;
    private int emptyFrom; // index of first symbol after which there are all empty symbols
    private boolean isEpsilon;

    public Production(Symbol lhs, List<Symbol> rhs, int id) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.id = id;
        emptyFrom = 0;
        isEpsilon = rhs.get(0).equals(Symbol.EPS_SYMBOL);
    }

    public void annotate() {
        for (int idx = 0; idx < rhs.size(); idx++) {
            if (!rhs.get(idx).isEmpty()) {
                emptyFrom = idx + 1;
            }
        }
    }

    public boolean isEpsilonProduction() {
        return isEpsilon;
    }

    public int emptyFrom() {
        return emptyFrom;
    }

    public Symbol getLHS() {
        return lhs;
    }

    public List<Symbol> getRHS() {
        return rhs;
    }

    public boolean isEmpty() {
        for (Symbol s : rhs) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
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

    @Override
    public int compareTo(Production o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        }
        Production p = (Production) obj;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
