package hr.fer.zemris.ppj.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * @return the dfa
     */
    public DFA<Integer, Sym> getDfa() {
        return dfa;
    }

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
