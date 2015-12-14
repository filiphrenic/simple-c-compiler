package hr.fer.zemris.ppj.automaton;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    private Map<St, Map<Sym, Set<St>>> transitions;
    private Map<St, Set<St>> epsilonTransitions;
    private boolean accepts;

    private boolean valid;
    private Mapper<St> mapper;
    private BitSet calculating;
    private Map<Integer, BitSet> epsilonEnvironment;

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
    public EpsilonNFA(St startState, St finalState, Map<St, Map<Sym, Set<St>>> transitions,
            Map<St, Set<St>> epsilonTransitions) {
        this.startState = startState;
        this.finalState = finalState;
        this.transitions = transitions;
        this.epsilonTransitions = epsilonTransitions;
        currentStates = new LinkedHashSet<>();
        currentStates.add(startState);
    }

    @Override
    public void consume(Sym symbol) {
        Set<St> states = new LinkedHashSet<>();
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
     * Transforms this epsilon non deterministic finite automaton to a automaton
     * without epsilon transitions.
     * 
     * @return equivalent nfa
     */
    public NFA<St, Sym> toNFA() {

        // TODO switch to bitsets

        Set<St> acceptableStates = new LinkedHashSet<>();
        acceptableStates.add(finalState);
        Set<St> epsStartState = epsilonEnv(startState);
        if (epsStartState.contains(finalState)) {
            acceptableStates.add(startState);
        }
        Map<St, Map<Sym, Set<St>>> nfaTransitions = new HashMap<>();

        Set<St> allStates = new LinkedHashSet<>(transitions.keySet());
        allStates.addAll(epsilonTransitions.keySet());

        for (St state : allStates) {
            Map<Sym, Set<St>> trans = new HashMap<>();
            for (St st : epsilonEnv(state)) {
                Map<Sym, Set<St>> currState = transitions.get(st);
                if (currState == null) {
                    continue;
                }
                for (Entry<Sym, Set<St>> e : currState.entrySet()) {
                    Set<St> before = trans.get(e.getKey());
                    if (before == null) {
                        before = new LinkedHashSet<>();
                    }
                    before.addAll(epsilonEnv(e.getValue()));
                    trans.put(e.getKey(), before);
                }
            }
            nfaTransitions.put(state, trans);
        }

        return new NFA<St, Sym>(epsStartState, acceptableStates, nfaTransitions);
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

        for (Entry<St, Map<Sym, Set<St>>> e1 : enfa.transitions.entrySet()) {
            St fromState = e1.getKey();
            for (Entry<Sym, Set<St>> e2 : e1.getValue().entrySet()) {
                Sym symbol = e2.getKey();
                for (St toState : e2.getValue()) {
                    addTransition(fromState, symbol, toState);
                }
            }
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
        Map<Sym, Set<St>> transitionMap = transitions.get(left);
        if (transitionMap == null) {
            transitionMap = new HashMap<>();
        }
        Set<St> states = transitionMap.get(symbol);
        if (states == null) {
            states = new LinkedHashSet<>();
        }
        states.add(right);
        transitionMap.put(symbol, states);
        transitions.put(left, transitionMap);

        valid = false;
    }

    /**
     * Add epsilon transition from left to right state
     * 
     * @param left left state
     * @param right right state
     */
    public void addEpsilonTransition(St left, St right) {
        Set<St> states = epsilonTransitions.get(left);
        if (states == null) {
            states = new LinkedHashSet<>();
        }
        states.add(right);
        epsilonTransitions.put(left, states);

        valid = false;
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

    /**
     * Updates the current states to the epsilon environment of those states.
     */
    private void updateCurrentStates() {
        checkValid();

        BitSet b = new BitSet(calculating.size());
        for (St s : currentStates) {
            b.or(getEpsilonEnvironment(s));
        }

        accepts = false;
        currentStates = bitsetToSet(b);
        if (currentStates.contains(finalState)) {
            accepts = true;
        }
    }

    /**
     * Calculates epsilon environment of a given state
     * 
     * @param state
     * @return epsilon environment
     */
    private Set<St> epsilonEnv(St state) {
        checkValid();
        BitSet b = getEpsilonEnvironment(state);
        return bitsetToSet(b);
    }

    /**
     * Bulk epsilon environment.
     * 
     * @param states
     * @return union of epsilon environments of all states
     */
    private Set<St> epsilonEnv(Set<St> states) {
        checkValid();
        BitSet b = new BitSet(calculating.size());
        for (St s : states) {
            b.or(getEpsilonEnvironment(s));
        }
        return bitsetToSet(b);
    }

    /**
     * Returns the epsilon environment of the given state.
     * 
     * @param state
     * @return epsilon environment
     */
    private BitSet getEpsilonEnvironment(St state) {
        int x = mapper.getForValue(state);
        if (!epsilonEnvironment.containsKey(x)) {
            calculating.set(x);
            epsilonEnvironment.put(x, findEpsilonEnvironment(x));
            calculating.clear(x);
        }
        return epsilonEnvironment.get(x);
    }

    /**
     * Finds the epsilon environment of a given states index.
     * 
     * @param x state index
     * @return epsilon environment
     */
    private BitSet findEpsilonEnvironment(int x) {
        BitSet b = new BitSet();
        b.set(x);
        Set<St> states = epsilonTransitions.get(mapper.getForIndex(x));
        if (states != null) {
            for (St s : states) {
                int y = mapper.getForValue(s);
                b.set(y);
                if (!calculating.get(y)) {
                    b.or(getEpsilonEnvironment(s));
                }
            }
        }
        return b;
    }

    /**
     * Builds a {@link Mapper} object for this automatons states.
     * 
     * @param transitions
     * @param epsilonTransitions
     */
    private void buildMapper(Map<St, Map<Sym, Set<St>>> transitions,
            Map<St, Set<St>> epsilonTransitions) {
        Set<St> states = new LinkedHashSet<>();
        for (Entry<St, Map<Sym, Set<St>>> e1 : transitions.entrySet()) {
            states.add(e1.getKey());
            for (Entry<Sym, Set<St>> e2 : e1.getValue().entrySet()) {
                states.addAll(e2.getValue());
            }
        }
        for (Entry<St, Set<St>> e : epsilonTransitions.entrySet()) {
            states.add(e.getKey());
            states.addAll(e.getValue());
        }

        epsilonEnvironment = new HashMap<>();
        calculating = new BitSet(states.size());
        mapper = new Mapper<>(states);
    }

    /**
     * Invariant check. If automaton is in invalid state, puts it in a valid
     * state.
     */
    private void checkValid() {
        if (!valid) {
            buildMapper(transitions, epsilonTransitions);
            valid = true;
        }
    }

    /**
     * Turns bitset representation to set.
     * 
     * @param b bitset with states
     * @return set
     */
    private Set<St> bitsetToSet(BitSet b) {
        Set<St> states = new LinkedHashSet<>();
        for (int idx = b.nextSetBit(0); idx >= 0; idx = b.nextSetBit(idx + 1)) {
            states.add(mapper.getForIndex(idx));
        }
        return states;
    }

    @Override
    public String toString() {
        Set<St> states = new LinkedHashSet<>(transitions.keySet());
        states.addAll(epsilonTransitions.keySet());
        int numStates = states.size();

        int numTransitions = 0;
        for (St st : epsilonTransitions.keySet()) {
            numTransitions += epsilonTransitions.get(st).size();
        }
        for (St st : transitions.keySet()) {
            for (Entry<Sym, Set<St>> e : transitions.get(st).entrySet()) {
                numTransitions += e.getValue().size();
            }
        }

        return "ENFA:[states=" + numStates + "; transitions=" + numTransitions + "]";
    }

}
