package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * Implementation of a epsilon non deterministic finite state automaton.
 * 
 * @author fhrenic
 */
public class EpsilonNFA<St, Sym> implements Automaton<Sym> {

    private static final long serialVersionUID = -3236943639300997444L;

    private St startState;
    private St finalState;
    private Set<St> currentStates;
    private Map<St, Map<Sym, List<St>>> transitions;
    private Map<St, List<St>> epsilonTransitions;
    private boolean accepts;

    /**
     * Creates a new automaton with given left and right state. This should be
     * called only after you have added the transitions to the handler. If it's
     * done the other way around, it may not work properly.
     * 
     * @param startState starting state
     * @param finalState final state
     * @param transitions a 'normal' transitions map
     * @param epsilonTransitions epsilon transitions map
     */
    public EpsilonNFA(St startState, St finalState, Map<St, Map<Sym, List<St>>> transitions,
            Map<St, List<St>> epsilonTransitions) {
        this.startState = startState;
        this.finalState = finalState;
        this.transitions = transitions;
        this.epsilonTransitions = epsilonTransitions;
        currentStates = new TreeSet<>();
        currentStates.add(startState);
    }

    @Override
    public void consume(Sym symbol) {
        Set<St> states = new TreeSet<>();
        for (St state : currentStates) {
            Map<Sym, List<St>> transitionMap = transitions.get(state);
            if (transitionMap == null) {
                continue;
            }
            List<St> transitionStates = transitionMap.get(symbol);
            if (transitionStates != null) {
                states.addAll(transitionStates);
            }
        }
        currentStates = states;
        updateCurrentStates();
    }

    @Override
    public void reset() {
        currentStates.clear();
        currentStates.add(startState);
        updateCurrentStates();
    }

    @Override
    public boolean accepts() {
        return accepts;
    }

    @Override
    public boolean isDead() {
        return currentStates.isEmpty();
    }

    /**
     * This enfa takes all transitions of the given enfa
     * 
     * @param enfa enfa to take transitions from
     */
    protected void adopt(EpsilonNFA<St, Sym> enfa) {
        for (St fromState : enfa.epsilonTransitions.keySet()) {
            for (St toState : enfa.epsilonTransitions.get(fromState)) {
                addEpsilonTransition(fromState, toState);
            }
        }

        for (Entry<St, Map<Sym, List<St>>> e1 : enfa.transitions.entrySet()) {
            St fromState = e1.getKey();
            for (Entry<Sym, List<St>> e2 : e1.getValue().entrySet()) {
                Sym symbol = e2.getKey();
                for (St toState : e2.getValue()) {
                    addTransition(fromState, symbol, toState);
                }

            }
        }
    }

    /**
     * Updates the current states to the epsilon environment of those states.
     */
    protected void updateCurrentStates() {
        // epsilon environment
        accepts = false;
        Set<St> states;
        do {
            states = new TreeSet<>();
            for (St state : currentStates) {
                if (state == finalState) {
                    accepts = true;
                }
                List<St> epsStates = epsilonTransitions.get(state);
                if (epsStates != null) {
                    states.addAll(epsStates);
                }
            }
        } while (currentStates.addAll(states));

        if (!accepts && currentStates.contains(finalState)) {
            accepts = true;
        }
    }

    /**
     * Add a transition from left to right state via given symbol
     * 
     * @param left left state
     * @param symbol symbol
     * @param right right state
     */
    public void addTransition(St left, Sym symbol, St right) {
        Map<Sym, List<St>> transitionMap = transitions.get(left);
        if (transitionMap == null) {
            transitionMap = new HashMap<>();
        }
        List<St> states = transitionMap.get(symbol);
        if (states == null) {
            states = new LinkedList<>();
        }
        states.add(right);
        transitionMap.put(symbol, states);
        transitions.put(left, transitionMap);
    }

    /**
     * Add epsilon transition from left to right state
     * 
     * @param left left state
     * @param right right state
     */
    public void addEpsilonTransition(St left, St right) {
        List<St> states = epsilonTransitions.get(left);
        if (states == null) {
            states = new LinkedList<>();
        }
        states.add(right);
        epsilonTransitions.put(left, states);
    }

    /**
     * @return start state
     */
    public St getStartState() {
        return startState;
    }

    /**
     * @return final state
     */
    public St getFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        Set<St> states = new TreeSet<>(transitions.keySet());
        states.addAll(epsilonTransitions.keySet());
        int numStates = states.size();

        int numTransitions = 0;
        for (St st : epsilonTransitions.keySet()) {
            numTransitions += epsilonTransitions.get(st).size();
        }
        for (St st : transitions.keySet()) {
            for (Entry<Sym, List<St>> e : transitions.get(st).entrySet()) {
                numTransitions += e.getValue().size();
            }
        }

        return "ENFA:[states=" + numStates + "; transitions=" + numTransitions + "]";
    }

}
