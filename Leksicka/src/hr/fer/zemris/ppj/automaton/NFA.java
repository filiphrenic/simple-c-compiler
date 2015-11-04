package hr.fer.zemris.ppj.automaton;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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

    /**
     * Returns a set of states that are accessible from at least one of the
     * given states via given symbol
     * 
     * @param states
     * @param symbol
     * @return states
     */
    private Set<St> applyTransition(Set<St> states, Sym symbol) {
        Set<St> trans = new TreeSet<>();
        for (St state : states) {
            Map<Sym, Set<St>> transitionMap = transitions.get(state);
            if (transitionMap == null) {
                continue;
            }
            Set<St> transitionStates = transitionMap.get(symbol);
            if (transitionStates == null) {
                continue;
            }
            trans.addAll(transitionStates);
        }
        return trans;
    }

    /**
     * All symbols for which there is at least one transition from one of the
     * states in the set.
     * 
     * @param states
     * @return symbols
     */
    private Set<Sym> getSymbols(Set<St> states) {
        Set<Sym> symbols = new TreeSet<>();
        for (St s : states) {
            Map<Sym, Set<St>> map = transitions.get(s);
            if (map != null) {
                symbols.addAll(map.keySet());
            }
        }
        return symbols;
    }

    /**
     * Converts a nfa to a dfa.
     * 
     * @param nfa
     * @return dfa
     */
    public DFA<Integer, Sym> toDFA() {
        // for dfa
        int state = 0;
        Set<Integer> acceptableStates = new TreeSet<>();
        Map<Integer, Map<Sym, Integer>> dfaTransitions = new HashMap<>();

        // helper : set of states -> alias state
        Map<Set<St>, Integer> aliases = new HashMap<>();

        // initialization
        LinkedList<Set<St>> queue = new LinkedList<>();
        Set<St> states = Collections.singleton(startState);
        aliases.put(states, state++);

        queue.add(states);
        while (!queue.isEmpty()) {
            states = queue.removeFirst();
            Integer alias = aliases.get(states); // will exist

            Set<Sym> symbols = getSymbols(states);
            Map<Sym, Integer> transitions = new HashMap<>();

            for (Sym symbol : symbols) {
                Set<St> transitionStates = applyTransition(states, symbol);
                Integer transAlias = aliases.get(transitionStates);

                if (transAlias == null) {
                    transAlias = state++;
                    aliases.put(transitionStates, transAlias);
                    queue.add(transitionStates);

                    for (St st : transitionStates) {
                        if (this.acceptableStates.contains(st)) {
                            acceptableStates.add(transAlias);
                            break;
                        }
                    }
                }
                transitions.put(symbol, transAlias);
            }
            dfaTransitions.put(alias, transitions);
        }

        return new DFA<Integer, Sym>(0, acceptableStates, dfaTransitions);
    }

}
