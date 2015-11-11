package hr.fer.zemris.ppj.sintax.grammar;

import java.util.List;

import hr.fer.zemris.ppj.stream.SintaxInputParser;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production {

    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;
    private int emptyFrom; // index of first symbol after which there are all empty symbols

    public Production(Symbol lhs, List<Symbol> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        emptyFrom = 0;
    }

    public boolean isEpsilonProduction() {
        return rhs.get(0).equals(SintaxInputParser.EPS_SYMBOL);
    }

    public void annotate() {
        for (int idx = 0; idx < rhs.size(); idx++) {
            if (!rhs.get(idx).isEmpty()) {
                emptyFrom = idx + 1;
            }
        }
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
