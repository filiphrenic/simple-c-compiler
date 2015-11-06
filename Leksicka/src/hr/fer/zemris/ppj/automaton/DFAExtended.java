package hr.fer.zemris.ppj.automaton;

import java.util.Map;
import java.util.Set;

/**
 * @author fhrenic
 */
public class DFAExtended<St, Sym> {

    private DFA<Integer, Sym> dfa;
    private Map<Integer, Set<St>> aliases; // set should be linked hash set

    public DFAExtended(DFA<Integer, Sym> dfa, Map<Integer, Set<St>> aliases) {
        this.dfa = dfa;
        this.aliases = aliases;
    }

    // TODO will be used for generating tables

    /**
     * @return the dfa
     */
    public DFA<Integer, Sym> getDfa() {
        return dfa;
    }

}
