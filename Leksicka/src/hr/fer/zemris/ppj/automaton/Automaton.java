package hr.fer.zemris.ppj.automaton;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author fhrenic
 */
public class Automaton {

    private static AutomatonHandler handler = new AutomatonHandler();

    public static void setHandler(AutomatonHandler handler) {
        Automaton.handler = handler;
    }

    private int leftState;
    private int rightState;
    private Set<Integer> currentStates;
    private boolean accepts;

    protected Automaton(int leftState, int rightState) {
        this(leftState, rightState, new TreeSet<Integer>(), true);
    }

    protected Automaton(int leftState, int rightState, Set<Integer> currentStates, boolean update) {
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
     * Returns <code>true</code> if automaton is in acceptable state.
     * 
     * @return
     */
    public boolean accepts() {
        return accepts;
    }

    public void consume(char symbol) {
        if (accepts) {
            accepts = false;
            return;
        }

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
     * Puts this automaton in accepting state and it can't go out of it.
     */
    protected void setAcceptable() {
        currentStates = new TreeSet<>();
        accepts = true;
    }

    protected void addStates(Set<Integer> states) {
        currentStates.addAll(states);
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
                changed |= states.addAll(handler.getEpsilonStates(state));
            }
        }

        if (states.contains(rightState)) {
            setAcceptable();
        } else {
            currentStates = states;
        }
    }

}
