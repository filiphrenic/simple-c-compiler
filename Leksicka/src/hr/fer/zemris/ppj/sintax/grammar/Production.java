package hr.fer.zemris.ppj.sintax.grammar;

import java.util.List;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production {

    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;

    public Symbol getLeftHandSide() {
        return lhs;
    }

    public List<Symbol> getRightHandSide() {
        return rhs;
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
