package hr.fer.zemris.ppj.automaton;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    // minimization
    /**
     * Minimizes this dfa.
     */
    public void minimize() {
        removeUnreachableStates();
        joinEquivalentStates();
    }

    /**
     * Removes unreachable states. Only saves memory.
     */
    private void removeUnreachableStates() {
        Set<St> reachable = new TreeSet<>();
        reachable.add(startState);

        Set<St> help;
        do {
            help = new TreeSet<>();
            for (St state : reachable) {
                Map<Sym, St> map = transitions.get(state);
                if (map == null) {
                    continue;
                }
                help.addAll(map.values());
            }
        } while (reachable.addAll(help));

        for (St state : reachable) {
            transitions.remove(state);
        }
    }

    /**
     * Join equivalent states into one.
     */
    private void joinEquivalentStates() {
        // TODO
    }

}
