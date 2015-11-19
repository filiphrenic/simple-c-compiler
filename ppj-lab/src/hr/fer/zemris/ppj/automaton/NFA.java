package hr.fer.zemris.ppj.automaton;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of a non deterministic finite state automaton.
 * 
 * @author fhrenic
 */
public class NFA<St, Sym> implements Automaton<Sym> {

    private static final long serialVersionUID = -377105148250534821L;

    public Set<St> startingStates;
    public Set<St> acceptableStates;
    public Set<St> currentStates;
    public Map<St, Map<Sym, Set<St>>> transitions;

    /**
     * Creates a new automaton with given properties.
     * 
     * @param startingStates starting states
     * @param acceptableStates final state
     * @param transitions a 'normal' transitions map
     * @param epsilonTransitions epsilon transitions map
     */
    public NFA(Set<St> startingStates, Set<St> acceptableStates,
            Map<St, Map<Sym, Set<St>>> transitions) {
        this.startingStates = startingStates;
        this.acceptableStates = acceptableStates;
        this.transitions = transitions;
        reset();
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
    }

    @Override
    public void reset() {
        currentStates = new TreeSet<>(startingStates);
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
        Set<St> trans = new LinkedHashSet<>();
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
        Set<Sym> symbols = new LinkedHashSet<>();
        for (St s : states) {
            Map<Sym, Set<St>> map = transitions.get(s);
            if (map != null) {
                symbols.addAll(map.keySet());
            }
        }
        return symbols;
    }

    /**
     * Converts a nfa to an extended dfa.
     * 
     * @param nfa
     * @return dfa
     */
    public DFAExtended<St, Sym> toDFA() {
        // for dfa
        int state = 0;
        Set<Integer> acceptableStates = new LinkedHashSet<>();
        Map<Integer, Map<Sym, Integer>> dfaTransitions = new HashMap<>();
        Map<Integer, Set<St>> dfaAliases = new HashMap<>();

        // helper : set of states -> alias state
        Map<Set<St>, Integer> aliases = new HashMap<>();

        // initialization
        Queue<Set<St>> queue = new LinkedList<>();
        Set<St> states = new LinkedHashSet<>(startingStates);
        aliases.put(states, state);
        dfaAliases.put(state++, states);

        queue.add(states);
        while (!queue.isEmpty()) {
            states = queue.poll();
            Integer alias = aliases.get(states); // will exist

            Set<Sym> symbols = getSymbols(states);
            Map<Sym, Integer> transitions = new HashMap<>();

            for (Sym symbol : symbols) {
                Set<St> transitionStates = applyTransition(states, symbol);
                Integer transAlias = aliases.get(transitionStates);

                if (transAlias == null) {
                    transAlias = state++;
                    aliases.put(transitionStates, transAlias);
                    dfaAliases.put(transAlias, transitionStates);
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

        DFA<Integer, Sym> dfa = new DFA<Integer, Sym>(0, acceptableStates, dfaTransitions);
        return new DFAExtended<>(dfa, dfaAliases);
    }

}
