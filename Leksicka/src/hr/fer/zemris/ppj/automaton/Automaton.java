package hr.fer.zemris.ppj.automaton;

import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents an epsilon non deterministic finite automaton.
 * 
 * @author fhrenic
 */
public class Automaton {

    // if speed optimization needed:
    //  - consider optimizing what times is the updateStates() called

    public static void main(String[] args) {
        Automaton a = null;
        System.out.println(a.getCurrentStates());
        a.consume('c');
        System.out.println("\nconsumed c");
        System.out.println(a.getCurrentStates());
        a.consume('d');
        System.out.println("\nconsumed d");
        System.out.println(a.getCurrentStates());
    }

    /**
     * This object is used for providing and storing transitions. Automatons can
     * use transitions via this object.
     */
    private static AutomatonHandler handler = new AutomatonHandler();

    /**
     * Sets a new handler. This method should be called when you have an
     * existing handler (generated and read from a file).
     * 
     * @param handler new handler
     */
    public static void setHandler(AutomatonHandler handler) {
        Automaton.handler = handler;
    }

    private int leftState;
    private int rightState; // this state is the only final state
    private Set<Integer> currentStates;
    private boolean accepts;

    /**
     * Creates a new automaton with given left and right state. This should be
     * called only after you have added the transitions to the handler. If it's
     * done the other way around, it may not work properly.
     * 
     * @param leftState starting state
     * @param rightState final state
     */
    protected Automaton(int leftState, int rightState) {
        this.leftState = leftState;
        this.rightState = rightState;
        currentStates = new TreeSet<Integer>();
        currentStates.add(leftState);
        updateCurrentStates();
    }

    /**
     * Returns <code>true</code> if automaton is in acceptable state.
     * 
     * @return <code>true</code> if automaton accepts a string
     */
    public boolean accepts() {
        return accepts;
    }

    /**
     * Applies transitions based on the given symbol.
     * 
     * @param symbol transition symbol
     */
    public void consume(char symbol) {
        Set<Integer> states = new TreeSet<>();
        for (Integer state : currentStates) {
            Integer transitionState = handler.getNormalStates(state).get(symbol);
            if (transitionState != null) {
                states.add(transitionState);
            }
        }
        currentStates = states;
        updateCurrentStates();
    }

    /**
     * Adds a set of states to the current states
     * 
     * @param states states to add
     */
    protected void addStates(Set<Integer> states) {
        currentStates.addAll(states);
        updateCurrentStates();
    }

    /**
     * @return the leftState
     */
    protected int leftState() {
        return leftState;
    }

    /**
     * @return the rightState
     */
    protected int rightState() {
        return rightState;
    }

    /**
     * @return the currentStates
     */
    protected Set<Integer> getCurrentStates() {
        return currentStates;
    }

    /**
     * Updates the current states to the epilon environment of those states.
     */
    private void updateCurrentStates() {
        // epsilon environment
        accepts = false;

        Set<Integer> states = new TreeSet<>(currentStates);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Integer state : currentStates) {
                if (state == rightState) {
                    accepts = true;
                }
                changed |= states.addAll(handler.getEpsilonStates(state));
            }
        }
        if (!accepts && states.contains(rightState)) {
            accepts = true;
        }
        currentStates = states;
    }

}
