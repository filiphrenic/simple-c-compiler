package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides various static methods for automaton creation from regexes.
 * 
 * @author fhrenic
 */
public class AutomatonCreator {

    private static final char EPS = '$';

    // state for automaton creation
    private static int state = 0;

    // regular definition -> regex
    private static Map<String, String> regularDefinitions = new HashMap<>();

    /**
     * Adds a regular definition to the handler so it can insert it into a
     * regex.
     * 
     * @param regDefName it's name
     * @param regex regex it represents
     */
    public static void addRegularDefinition(String regDefName, String regex) {
        regularDefinitions.put(regDefName, prepareRegex(regex));
    }

    /**
     * Creates a new automaton for a given <code>regex</code>.
     * 
     * @param regex regular expression used to create an automaton
     */
    public static EpsilonNFA<Integer, Character> fromString(String regex) {
        return transform(prepareRegex(regex));
    }

    /**
     * Returns a new, unique state.
     * 
     * @return state
     */
    private static Integer getNewState() {
        return Integer.valueOf(state++);
    }

    /**
     * Removes regular definitions from regex, swaps them with real regular
     * expressions.
     * 
     * @param regex regular expression
     * @return adjusted regular expression
     */
    private static String prepareRegex(String regex) {
        StringBuilder sb = new StringBuilder();
        int len = regex.length();
        for (int idx = 0; idx < len; idx++) {
            if (regex.charAt(idx) == '{' && AutomatonUtility.isOperator(regex, idx)) {
                int cidx = AutomatonUtility.findCloser(regex, '{', '}', idx);
                String regdef = regex.substring(idx + 1, cidx);
                String reg = regularDefinitions.get(regdef);
                sb.append('(' + reg + ')');
                idx = cidx;
            } else {
                sb.append(regex.charAt(idx));
            }
        }
        return sb.toString();
    }

    /**
     * Transforms the given regular expression into an {@link EpsilonNFAOld}
     * 
     * @param regex regular expression
     * @return automaton
     */
    private static EpsilonNFA<Integer, Character> transform(String regex) {
        EpsilonNFA<Integer, Character> enfa = getEmptyEnfa();

        List<String> choices = AutomatonUtility.splitChoices(regex);
        if (choices.size() > 1) {
            for (String choice : choices) {
                EpsilonNFA<Integer, Character> tmp = transform(choice);
                enfa.adopt(tmp);
                enfa.addEpsilonTransition(enfa.getStartState(), tmp.getStartState());
                enfa.addEpsilonTransition(tmp.getFinalState(), enfa.getFinalState());
            }
        } else {

            boolean prefixed = false;
            int len = regex.length();
            int lastState = enfa.getStartState();

            for (int idx = 0; idx < len; idx++) {
                int state1, state2;
                char symbol = regex.charAt(idx);
                if (prefixed) {
                    prefixed = false;
                    char escape = AutomatonUtility.unescape(symbol);
                    state1 = getNewState();
                    state2 = getNewState();
                    enfa.addTransition(state1, escape, state2);
                } else {
                    if (symbol == '\\') {
                        prefixed = true;
                        continue;
                    }
                    if (symbol == '(') {
                        int closing = AutomatonUtility.findCloser(regex, '(', ')', idx);
                        String subs = regex.substring(idx + 1, closing);
                        EpsilonNFA<Integer, Character> tmp = transform(subs);
                        state1 = tmp.getStartState();
                        state2 = tmp.getFinalState();
                        enfa.adopt(tmp);
                        idx = closing;
                    } else {
                        state1 = getNewState();
                        state2 = getNewState();
                        if (symbol == EPS) {
                            enfa.addEpsilonTransition(state1, state2);
                        } else {
                            enfa.addTransition(state1, symbol, state2);
                        }
                    }
                }

                // KLEENE
                if (idx + 1 < len && regex.charAt(idx + 1) == '*') {
                    int stateTmp1 = state1;
                    int stateTmp2 = state2;
                    state1 = getNewState();
                    state2 = getNewState();
                    enfa.addEpsilonTransition(state1, stateTmp1);
                    enfa.addEpsilonTransition(state1, state2);
                    enfa.addEpsilonTransition(stateTmp2, stateTmp1);
                    enfa.addEpsilonTransition(stateTmp2, state2);
                    idx++;
                }

                // CONNECT TO AUTOMATON
                enfa.addEpsilonTransition(lastState, state1);
                lastState = state2;
            }
            // CONNECT TO LAST STATE
            enfa.addEpsilonTransition(lastState, enfa.getFinalState());
        }
        enfa.updateCurrentStates();
        return enfa;
    }

    /**
     * Returns a new, empty (no transitions) enfa.
     * 
     * @return empty enfa
     */
    private static EpsilonNFA<Integer, Character> getEmptyEnfa() {
        return new EpsilonNFA<Integer, Character>(getNewState(), getNewState(),
                new HashMap<Integer, Map<Character, Set<Integer>>>(),
                new HashMap<Integer, Set<Integer>>());
    }

}
