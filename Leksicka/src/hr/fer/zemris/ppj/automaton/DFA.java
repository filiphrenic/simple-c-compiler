package hr.fer.zemris.ppj.automaton;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of a deterministic finite state automaton.
 * 
 * @author fhrenic
 */
public class DFA<St, Sym> implements Automaton<Sym> {

    private static final long serialVersionUID = -2817868665059246120L;

    private St startState;
    private Set<St> acceptableStates;
    private Map<St, Map<Sym, St>> transitions;
    private St currentState;

    /**
     * Creates a new deterministic finite state automaton.
     * 
     * @param startState starting state
     * @param acceptableStates all acceptable states
     * @param transitions transition map
     */
    public DFA(St startState, Set<St> acceptableStates, Map<St, Map<Sym, St>> transitions) {
        this.startState = startState;
        this.acceptableStates = acceptableStates;
        this.transitions = transitions;
        reset();
    }

    /**
     * Returns dfa transitions.
     * @return transitions
     */
    public Map<St, Map<Sym, St>> getTransitions() {
        return transitions;
    }

    @Override
    public void consume(Sym symbol) {
        if (currentState == null) {
            return;
        }
        Map<Sym, St> transition = transitions.get(currentState);
        if (transition == null) {
            currentState = null;
            return;
        }
        currentState = transition.get(symbol);
    }

    @Override
    public void reset() {
        currentState = startState;
    }

    @Override
    public boolean accepts() {
        if (currentState == null) {
            return false;
        } else {
            return acceptableStates.contains(currentState);
        }
    }

    @Override
    public boolean isDead() {
        return currentState == null;
    }

}
