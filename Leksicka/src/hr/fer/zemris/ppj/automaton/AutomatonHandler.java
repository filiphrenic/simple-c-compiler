package hr.fer.zemris.ppj.automaton;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used primarily to store transitions for all automates. It
 * allows us to have a forest of automatons (if we think of an automaton as a
 * graph). That way we don't get duplicate edges or nodes.
 * 
 * The only methods this class provides are used for creating automatons from
 * regexes and adding choices to automatons.
 * 
 * @author fhrenic
 */
public class AutomatonHandler implements Serializable {

    private static final long serialVersionUID = -3143699593378714639L;
    private static final char EPS = '$';

    //    public static void main(String[] args) {
    //        
    //        AutomatonHandler h = new AutomatonHandler();
    //        Automaton.setHandler(h);
    //        h.fromString("AaA", "a"); // regDef
    //        String regex = "bB|{a}|(cC)*";
    //        Automaton b = h.fromString(regex, null);
    //        System.out.println(b.accepts());
    //    }

    // main automaton representation, all automatons are in these maps
    private int state;
    // state -> ( symbol -> set of states )
    private HashMap<Integer, HashMap<Character, Integer>> transitions;
    // state -> set of states
    private HashMap<Integer, Set<Integer>> epsilonTransitions;
    // regular definition -> automaton
    private HashMap<String, Automaton> regularDefinitions;

    /**
     * Creates a new, empty automaton handler.
     */
    public AutomatonHandler() {
        state = 0;
        transitions = new HashMap<>();
        epsilonTransitions = new HashMap<>();
        regularDefinitions = new HashMap<>();
    }

    /**
     * Returns a new, unique state.
     * 
     * @return state
     */
    protected int getNewState() {
        return state++;
    }

    /**
     * Creates a new automaton for a given <code>regex</code>.
     * <code>regDefName</code> can be either <code>null</code> or a definitions
     * name. If it is <code>null</code>, regex isn't saved in the regdef table
     * 
     * @param regex regular expression used to create an automaton
     * @param regDefName name of the regular definition (if it's not a regular
     *            definition, pass <code>null</code>)
     */
    public Automaton fromString(String regex, String regDefName) {
        Automaton automaton = transform(regex);
        if (regDefName != null) {
            regularDefinitions.put(regDefName, automaton);
        }
        return automaton;
    }

    /**
     * Transforms the given regular expression into an {@link Automaton}
     * 
     * @param regex regula expression
     * @return automaton
     */
    private Automaton transform(String regex) {
        // System.out.println("REGEX -> " + regex);
        List<String> choices = AutomatonUtility.splitChoices(regex);
        int leftState = getNewState();
        int rightState = getNewState();

        if (choices.size() > 1) {
            for (String choice : choices) {
                Automaton tmp = transform(choice);
                addEpsilonTransition(leftState, tmp.leftState());
                addEpsilonTransition(tmp.rightState(), rightState);
            }
        } else {

            boolean prefixed = false;
            int len = regex.length();
            int lastState = leftState;

            for (int idx = 0; idx < len; idx++) {
                int state1, state2;
                char symbol = regex.charAt(idx);
                if (prefixed) {
                    prefixed = false;
                    char escape = AutomatonUtility.unescape(symbol);
                    state1 = getNewState();
                    state2 = getNewState();
                    addTransition(state1, state2, escape);
                } else {
                    if (symbol == '\\') {
                        prefixed = true;
                        continue;
                    }
                    if (symbol == '(' || symbol == '{') {
                        // (regex) or {regDef}
                        char closing = symbol == '(' ? ')' : '}';
                        int close = AutomatonUtility.findCloser(regex, closing, idx + 1);
                        String subs = regex.substring(idx + 1, close);
                        Automaton tmp = symbol == '(' ? transform(subs)
                                : regularDefinitions.get(subs);
                        state1 = tmp.leftState();
                        state2 = tmp.rightState();
                        idx = close;
                    } else {
                        state1 = getNewState();
                        state2 = getNewState();
                        if (symbol == EPS) {
                            addEpsilonTransition(state1, state2);
                        } else {
                            addTransition(state1, state2, symbol);
                        }
                    }
                }

                // KLEENE
                if (idx + 1 < len && regex.charAt(idx + 1) == '*') {
                    int stateTmp1 = state1;
                    int stateTmp2 = state2;
                    state1 = getNewState();
                    state2 = getNewState();
                    addEpsilonTransition(state1, stateTmp1);
                    addEpsilonTransition(state1, state2);
                    addEpsilonTransition(stateTmp2, stateTmp1);
                    addEpsilonTransition(stateTmp2, state2);
                    idx++;
                }

                // CONNECT TO AUTOMATON
                addEpsilonTransition(lastState, state1);
                lastState = state2;
            }
            // CONNECT TO LAST STATE
            addEpsilonTransition(lastState, rightState);
        }
        return new Automaton(leftState, rightState);
    }

    // ############################################################################

    /**
     * Adds an epsilon transition from one state (left) to another (right).
     * 
     * @param leftState left state
     * @param rightState right state
     */
    protected void addEpsilonTransition(int leftState, int rightState) {
        Set<Integer> states = getEpsilonStates(leftState);
        states.add(rightState);
        epsilonTransitions.put(leftState, states);
    }

    /**
     * This is a helper method. If there are some states that are accessible via
     * epsilon transitions from a given state, then they are returned.
     * Otherwise, an empty set is returned.
     * 
     * @param state state of interest
     * @return states that are accessible via epsilon transitions
     */
    protected Set<Integer> getEpsilonStates(int state) {
        Set<Integer> states = epsilonTransitions.get(state);
        if (states == null) {
            states = new TreeSet<>();
        }
        return states;
    }

    /**
     * Adds a transition via given symbol from one state (left) to another
     * (right).
     * 
     * @param leftState left state
     * @param rightState right state
     * @param symbol transition symbol
     */
    protected void addTransition(int leftState, int rightState, char symbol) {
        HashMap<Character, Integer> transition = getNormalStates(leftState);
        transition.put(symbol, rightState);
        transitions.put(leftState, transition);
    }

    /**
     * This is a helper method. If there are some states that are accessible via
     * symbol transitions from a given state, then they are returned. Otherwise,
     * an empty set is returned.
     * 
     * @param state state of interest
     * @return states that are accessible via symbol transitions
     */
    protected HashMap<Character, Integer> getNormalStates(int state) {
        HashMap<Character, Integer> transition = transitions.get(state);
        if (transition == null) {
            transition = new HashMap<>();
        }
        return transition;
    }

}
