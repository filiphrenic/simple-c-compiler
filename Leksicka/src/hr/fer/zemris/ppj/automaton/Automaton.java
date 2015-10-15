package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author fhrenic
 */
public class Automaton {

    private static int state = 0;
    // state -> ( symbol -> set of states )
    private static HashMap<Integer, HashMap<Character, Integer>> transitions;
    // state -> set of states
    private static HashMap<Integer, Set<Integer>> epsilonTransitions;
    // regular definition -> automaton
    private static HashMap<String, Automaton> regularDefinitions;

    static {
        transitions = new HashMap<>();
        epsilonTransitions = new HashMap<>();
        regularDefinitions = new HashMap<>();
    }

    private static int getState() {
        return state++;
    }

    // ########################################################################

    private int startState;
    private int endState;
    private Set<Integer> currentStates;
    private boolean accepts;

    private Automaton(int startState, int endState) {
        this.startState = startState;
        this.endState = endState;
        currentStates = new TreeSet<>();
        accepts = false;

        currentStates.add(startState);
        updateCurrentStates();
    }

    /**
     * Creates a new automaton for a given <code>regex</code>.
     * <code>regDefName</code> can be either <code>null</code> or a definitions
     * name. If it is <code>null</code>, regex isn't saved in the regdef table
     * 
     * @param regex
     * @param regDefName
     */
    public Automaton generate(String regex, String regDefName) {
        // TODO create automaton
        // use regDef table

        if (regDefName != null) {
            regularDefinitions.put(regDefName, this);
        }
        return this;
    }

    /**
     * TODO
     * USE THIS WHEN CREATING AN AUTOMATON FOR A RULE
     * @param regex
     */
    public void addRegex(String regex) {
        // this will be used in creating rules
        // ENka newAutomaton = new ENka(regex, false);

        /*
         * new automaton -> a & b (start & end states) 
         * this automaton -> s & e
         * 
         * add epsilon transitions: 
         * s -> e 
         * b -> e
         */
    }

    /**
     * Returns <code>true</code> if automaton is in acceptable state.
     * @return
     */
    public boolean isAcceptable() {
        return accepts;
    }

    public void consume(char symbol) {
        Set<Integer> states = new TreeSet<>();
        for (Integer state : currentStates) {
            Integer transitionState = getNormalStates(state).get(symbol);
            if (transitionState != null) {
                states.add(transitionState);
            }
        }
        currentStates = states;
        updateCurrentStates();
    }

    private void updateCurrentStates() {
        // epsilon environment
        Set<Integer> states = new TreeSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Integer state : currentStates) {
                if (state == endState) {
                    // don't need to traverse the graph any further, accepts
                    // this is the key optimization for speed
                    setAcceptable();
                    return;
                }
                changed |= states.addAll(getEpsilonStates(state));
            }
        }

        if (states.contains(endState)) {
            setAcceptable();
        } else {
            currentStates = states;
        }
    }

    private void setAcceptable() {
        currentStates = new TreeSet<>();
        accepts = true;
    }

    private static void addEpsilonTransition(int leftState, int rightState) {
        Set<Integer> states = getEpsilonStates(leftState);
        states.add(rightState);
        epsilonTransitions.put(leftState, states);
    }

    private static Set<Integer> getEpsilonStates(int state) {
        Set<Integer> states = epsilonTransitions.get(state);
        if (states == null) {
            states = new TreeSet<>();
        }
        return states;
    }

    private static void addTransition(int leftState, int rightState, char symbol) {
        HashMap<Character, Integer> transition = getNormalStates(leftState);
        transition.put(symbol, rightState);
        transitions.put(leftState, transition);
    }

    private static HashMap<Character, Integer> getNormalStates(int state) {
        HashMap<Character, Integer> transition = transitions.get(state);
        if (transition == null) {
            transition = new HashMap<>();
        }
        return transition;
    }

}
