package hr.fer.zemris.ppj.automaton;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of a non deterministic finite state automaton.
 * 
 * @author fhrenic
 */
public class NFA<St, Sym> implements Automaton<Sym> {

    private static final long serialVersionUID = -377105148250534821L;

    private St startState;
    private Set<St> acceptableStates;
    private Set<St> currentStates;
    private Map<St, Map<Sym, Set<St>>> transitions;

    /**
     * Creates a new automaton with given left and right state. This should be
     * called only after you have added the transitions to the handler. If it's
     * done the other way around, it may not work properly.
     * 
     * @param startState starting state
     * @param acceptableStates final state
     * @param transitions a 'normal' transitions map
     * @param epsilonTransitions epsilon transitions map
     */
    public NFA(St startState, Set<St> acceptableStates, Map<St, Map<Sym, Set<St>>> transitions) {
        this.startState = startState;
        this.acceptableStates = acceptableStates;
        this.transitions = transitions;
        currentStates = new TreeSet<>();
        reset();
    }

    public void debag(Sym s) {
        System.out.println("jedem " + s);
        consume(s);
        System.out.println("prihvacam? " + accepts());
    }

    @Override
    public void consume(Sym symbol) {
        Set<St> states = new TreeSet<>();
        for (St state : currentStates) {
            Map<Sym, Set<St>> transitionMap = transitions.get(state);
            if (transitionMap == null) {
                continue;
            }
            Set<St> transitionStates = transitionMap.get(symbol);
            if (transitionStates != null) {
                states.addAll(transitionStates);
            }
        }
        currentStates = states;
    }

    @Override
    public void reset() {
        currentStates.clear();
        currentStates.add(startState);
    }

    @Override
    public boolean accepts() {
        for (St state : acceptableStates) {
            if (currentStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDead() {
        return currentStates.isEmpty();
    }

}
