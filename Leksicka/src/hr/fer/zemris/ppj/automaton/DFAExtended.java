package hr.fer.zemris.ppj.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is an extended version of an ordinary {@link DFA}. It is basically an
 * dfa that has integer states, but additionally it contains a map of so called
 * aliases. So for each state it contains a set of aliases that are
 * "in that state".
 * 
 * @author fhrenic
 */
public class DFAExtended<St, Sym> {

    private DFA<Integer, Sym> dfa;
    private Map<Integer, Set<St>> aliases; // set should be linked hash set

    /**
     * Creates a new instance of this class.
     * 
     * @param dfa underlying dfa
     * @param aliases alias map
     */
    public DFAExtended(DFA<Integer, Sym> dfa, Map<Integer, Set<St>> aliases) {
        this.dfa = dfa;
        this.aliases = aliases;
    }

    /**
     * @return the dfa
     */
    public DFA<Integer, Sym> getDfa() {
        return dfa;
    }

    /**
     * @return the aliases
     */
    public Map<Integer, Set<St>> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Integer> states = new ArrayList<>(aliases.keySet());
        Collections.sort(states);

        for (Integer state : states) {
            sb.append(state);
            sb.append(' ');
            sb.append(dfa.getTransitions().get(state));
            sb.append('\n');

            List<St> aliasss = new ArrayList<>(aliases.get(state));
            Collections.sort(states);

            for (St alias : aliasss) {
                sb.append(' ');
                sb.append(alias);
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
