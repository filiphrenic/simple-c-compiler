package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Automaton {

    private static int state;

    // regular definition -> automaton
    private static HashMap<String, Automaton> regularDefinitions;

    static {
        state = 0;
        regularDefinitions = new HashMap<>();
    }

    private static int getState() {
        return state++;
    }

    private int startState;
    private int finalState;
    private Set<Integer> currentStates;

    // state -> ( symbol -> set of states )
    private HashMap<Integer, HashMap<Character, Integer>> transitions;
    // state -> set of states
    private HashMap<Integer, List<Integer>> epsilonTransitions;

    /**
     * Creates a new automaton for a given <code>regex</code>. 
     * <code>regDefName</code> can be either <code>null</code> or a definitions name.
     * If it is <code>null</code>, regex isn't saved in the regdef table
     * @param regex
     * @param regDefName
     */
    public Automaton(String regex, String regDefName) {
        // TODO create automaton
        // use regDef table

        if (regDefName != null) {
            regularDefinitions.put(regDefName, this);
        }
    }

    public void addRegex(String regex) {
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

    private Set<Integer> getEpsilonEnv(int state) {
        // epsilon environment
        return null;
    }

    public boolean isAcceptable() {
        return false;
    }

    public void consume(char symbol) {
        // maybe not void?
    }

}
