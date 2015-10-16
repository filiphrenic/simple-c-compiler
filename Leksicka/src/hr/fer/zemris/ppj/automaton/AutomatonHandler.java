package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
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
        Automaton automaton = null;

        // TODO create automaton

        if (regDefName != null) {
            regularDefinitions.put(regDefName, automaton);
        }
        return automaton;
    }

    /**
     * This method creates an automaton from regex and adds it as a choice to
     * the given automaton.
     * 
     * @param automaton automaton to which you will add a choice
     * @param regex used for creating choice-automaton
     */
    public void addChoice(Automaton automaton, String regex) {
        Automaton choice = fromString(regex, null);
        choice(automaton, choice);
    }

    // ############################################################################
    // BASIC AUTOMATONS

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
        return new Automaton(main.leftState(), main.rightState());
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
