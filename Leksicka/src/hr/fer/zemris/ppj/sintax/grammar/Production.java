package hr.fer.zemris.ppj.sintax.grammar;

import java.util.List;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production {

    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;

    public Production(Symbol lhs, List<Symbol> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        }
        Production p = (Production) obj;
        return lhs.equals(p.lhs) && rhs.equals(p.rhs);
    }

    @Override
    public int hashCode() {
        int hash = lhs.hashCode();
        for (Symbol s : rhs) {
            hash = 31 * hash + s.hashCode();
        }
        return hash;
    }

}
