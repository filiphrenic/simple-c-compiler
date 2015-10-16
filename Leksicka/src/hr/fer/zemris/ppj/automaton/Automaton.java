package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author fhrenic
 */
public class Automaton {

    public static void main(String[] args) {
        int N = 10;
        int[] states = new int[N];
        for (int i = 0; i < N; i++) {
            states[i] = getNewState();
        }

        Automaton a = epsilon();
        System.out.println(a.accepts);
        System.out.println(a.currentStates);

    }

    // main automaton representation, all automatons are in these maps
    private static int state = 0;
    // state -> ( symbol -> set of states )
    private static HashMap<Integer, HashMap<Character, Integer>> transitions;
    // state -> set of states
    private static HashMap<Integer, Set<Integer>> epsilonTransitions;
    // regular definition -> automaton
    private static HashMap<String, Automaton> regularDefinitions;

    static {
        state = 0;
        transitions = new HashMap<>();
        epsilonTransitions = new HashMap<>();
        regularDefinitions = new HashMap<>();
    }

    private static int getNewState() {
        return state++;
    }

    // ########################################################################

    private int leftState;
    private int rightState;
    private Set<Integer> currentStates;
    private boolean accepts;

    private Automaton(int leftState, int rightState) {
        this(leftState, rightState, new TreeSet<Integer>(), true);
    }

    private Automaton(int leftState, int rightState, Set<Integer> currentStates, boolean update) {
        this.leftState = leftState;
        this.rightState = rightState;
        accepts = false;

        this.currentStates = currentStates;
        this.currentStates.add(leftState);
        if (update) {
            updateCurrentStates();
        }
    }

    /**
     * Creates a new automaton for a given <code>regex</code>.
     * <code>regDefName</code> can be either <code>null</code> or a definitions
     * name. If it is <code>null</code>, regex isn't saved in the regdef table
     * 
     * @param regex
     * @param regDefName
     */
    public static Automaton fromString(String regex, String regDefName) {
        // TODO create automaton
        // use regDef table

        Automaton a = null;

        if (regDefName != null) {
            regularDefinitions.put(regDefName, a);
        }
        return a;
    }

    /**
     * TODO USE THIS WHEN CREATING AN AUTOMATON FOR A RULE
     * 
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
     * 
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
        Set<Integer> states = new TreeSet<>(currentStates);
        boolean changed = true;

        while (changed) {
            changed = false;
            for (Integer state : currentStates) {
                if (state == rightState) {
                    // don't need to traverse the graph any further, accepts
                    // this is the key optimization for speed
                    setAcceptable();
                    return;
                }
                changed |= states.addAll(getEpsilonStates(state));
            }
        }

        if (states.contains(rightState)) {
            setAcceptable();
        } else {
            currentStates = states;
        }
    }

    private void setAcceptable() {
        currentStates = new TreeSet<>();
        accepts = true;
    }

    // ############################################################################
    // SIMPLE AUTOMATONS

    /**
     * Builds a simple automaton that has two states and a transition between
     * them via given symbol.
     * 
     * @param symbol transition symbol
     * @return simple automaton
     */
    private static Automaton simple(char symbol) {
        int leftState = getNewState();
        int rightState = getNewState();
        addTransition(leftState, rightState, symbol);
        return new Automaton(leftState, rightState);
    }

    /**
     * Builds a simple epsilon automaton that has two states and an epsilon
     * transition between them.
     * 
     * @return epsilon automaton
     */
    private static Automaton epsilon() {
        int leftState = getNewState();
        int rightState = getNewState();
        addEpsilonTransition(leftState, rightState);
        return new Automaton(leftState, rightState);
    }

    /**
     * Builds an automaton given the left and right automatons that need to be
     * added.
     * 
     * @param left left automaton
     * @param right right automaton
     * @return resulting added automaton
     */
    private static Automaton add(Automaton left, Automaton right) {
        addEpsilonTransition(left.rightState, right.leftState);
        return new Automaton(left.leftState, right.rightState);
    }

    /**
     * Adds an automaton as a choice to the main automaton.
     * 
     * @param main main automaton that will have another choice
     * @param choice choice to add
     * @return modified main automaton
     */
    private static Automaton choice(Automaton main, Automaton choice) {
        addEpsilonTransition(main.leftState, choice.leftState);
        addEpsilonTransition(choice.rightState, main.rightState);
        if (choice.accepts) {
            main.setAcceptable();
        } else {
            main.currentStates.addAll(choice.currentStates);
        }
        return main;
    }

    /**
     * Builds a new automaton that is a Kleene star of a given automaton
     * 
     * @param automaton automaton used to create Kleene star automaton
     * @return new automaton
     */
    private static Automaton kleene(Automaton automaton) {
        int leftState = getNewState();
        int rightState = getNewState();
        addEpsilonTransition(leftState, automaton.leftState);
        addEpsilonTransition(leftState, rightState);
        addEpsilonTransition(automaton.rightState, rightState);
        addEpsilonTransition(automaton.rightState, automaton.leftState);
        return new Automaton(leftState, rightState);
    }

    // ############################################################################

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
