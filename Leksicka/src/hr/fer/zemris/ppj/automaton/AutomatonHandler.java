package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author fhrenic
 */
public class AutomatonHandler {

    public static void main(String[] args) {
    }

    // main automaton representation, all automatons are in these maps
    private int state;
    // state -> ( symbol -> set of states )
    private HashMap<Integer, HashMap<Character, Integer>> transitions;
    // state -> set of states
    private HashMap<Integer, Set<Integer>> epsilonTransitions;
    // regular definition -> automaton
    private HashMap<String, Automaton> regularDefinitions;

    public AutomatonHandler() {
        state = 0;
        transitions = new HashMap<>();
        epsilonTransitions = new HashMap<>();
        regularDefinitions = new HashMap<>();
    }

    public int getNewState() {
        return state++;
    }

    /**
     * Creates a new automaton for a given <code>regex</code>.
     * <code>regDefName</code> can be either <code>null</code> or a definitions
     * name. If it is <code>null</code>, regex isn't saved in the regdef table
     * 
     * @param regex
     * @param regDefName
     */
    public Automaton fromString(String regex, String regDefName) {
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

    // ############################################################################
    // SIMPLE AUTOMATONS

    /**
     * Builds a simple automaton that has two states and a transition between
     * them via given symbol.
     * 
     * @param symbol transition symbol
     * @return simple automaton
     */
    private Automaton simple(char symbol) {
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
    private Automaton epsilon() {
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
    private Automaton add(Automaton left, Automaton right) {
        addEpsilonTransition(left.rightState(), right.leftState());
        return new Automaton(left.leftState(), right.rightState());
    }

    /**
     * Adds an automaton as a choice to the main automaton.
     * 
     * @param main main automaton that will have another choice
     * @param choice choice to add
     * @return modified main automaton
     */
    private Automaton choice(Automaton main, Automaton choice) {
        addEpsilonTransition(main.leftState(), choice.leftState());
        addEpsilonTransition(choice.rightState(), main.rightState());
        if (choice.accepts()) {
            main.setAcceptable();
        } else {
            main.addStates(choice.getCurrentStates());
        }
        return main;
    }

    /**
     * Builds a new automaton that is a Kleene star of a given automaton
     * 
     * @param automaton automaton used to create Kleene star automaton
     * @return new automaton
     */
    private Automaton kleene(Automaton automaton) {
        int leftState = getNewState();
        int rightState = getNewState();
        addEpsilonTransition(leftState, automaton.leftState());
        addEpsilonTransition(leftState, rightState);
        addEpsilonTransition(automaton.rightState(), rightState);
        addEpsilonTransition(automaton.rightState(), automaton.leftState());
        return new Automaton(leftState, rightState);
    }

    // ############################################################################

    protected void addEpsilonTransition(int leftState, int rightState) {
        Set<Integer> states = getEpsilonStates(leftState);
        states.add(rightState);
        epsilonTransitions.put(leftState, states);
    }

    protected Set<Integer> getEpsilonStates(int state) {
        Set<Integer> states = epsilonTransitions.get(state);
        if (states == null) {
            states = new TreeSet<>();
        }
        return states;
    }

    protected void addTransition(int leftState, int rightState, char symbol) {
        HashMap<Character, Integer> transition = getNormalStates(leftState);
        transition.put(symbol, rightState);
        transitions.put(leftState, transition);
    }

    protected HashMap<Character, Integer> getNormalStates(int state) {
        HashMap<Character, Integer> transition = transitions.get(state);
        if (transition == null) {
            transition = new HashMap<>();
        }
        return transition;
    }

}
