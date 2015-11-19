package hr.fer.zemris.ppj.syntax.grammar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import hr.fer.zemris.ppj.automaton.DFAExtended;
import hr.fer.zemris.ppj.automaton.EpsilonNFA;
import hr.fer.zemris.ppj.syntax.LREntry;
import hr.fer.zemris.ppj.syntax.actions.AcceptAction;
import hr.fer.zemris.ppj.syntax.actions.LRAction;
import hr.fer.zemris.ppj.syntax.actions.MoveAction;
import hr.fer.zemris.ppj.syntax.actions.ReduceAction;

/**
 * This class represents a grammar. The most important method (for this
 * assignment) is the method that generates tables for LR parser.
 * 
 * @author fhrenic
 */
public class Grammar {

    private List<Symbol> terminalSymbols;
    private List<Symbol> nonTerminalSymbols;
    private Map<Symbol, List<Production>> productions;
    private Production startingProduction; // S' -> S

    private Map<Symbol, Integer> mapper;
    private Map<Integer, Symbol> rmapper;
    private BitSet[] starts;
    private int T;

    private Map<Integer, Map<Symbol, LRAction>> actions;
    private Map<Integer, Map<Symbol, Integer>> newStates;

    /**
     * Create a new instance of {@link Grammar}. This constructor basically does
     * all the work. You don't need to supply non-terminal symbols because they
     * should be keys of the productions map.
     * 
     * @param terminalSymbols terminal symbols
     * @param nonTerminalSymbols non terminal symbols
     * @param productions a map [non-terminal symbol => list of productions]
     * @param startingProduction starting production
     */
    public Grammar(List<Symbol> terminalSymbols, List<Symbol> nonTerminalSymbols,
            Map<Symbol, List<Production>> productions, Production startingProduction) {
        this.terminalSymbols = terminalSymbols;
        this.nonTerminalSymbols = nonTerminalSymbols;
        this.productions = productions;
        this.startingProduction = startingProduction;
        actions = new HashMap<>();
        newStates = new HashMap<>();

        annotateEmptySymbols();
        initMappers();
        initStartSets();
        buildParserTable(generateDFA());
    }

    /**
     * Returns the actions required by the parser.
     * 
     * @return action table
     */
    public Map<Integer, Map<Symbol, LRAction>> getActions() {
        return actions;
    }

    /**
     * Returns the new states required by the parser.
     * 
     * @return new state table
     */
    public Map<Integer, Map<Symbol, Integer>> getNewStates() {
        return newStates;
    }

    /**
     * Annotates symbols so it finds all empty symbols.
     */
    private void annotateEmptySymbols() {
        boolean foundNewEmpty = false;
        for (Symbol term : productions.keySet()) {
            if (term.isEmpty()) {
                continue;
            }
            for (Production prod : productions.get(term)) {
                if (prod.isEmpty()) {
                    term.setEmpty(true);
                    foundNewEmpty = true;
                    break;
                }
            }
        }
        if (foundNewEmpty) {
            annotateEmptySymbols();
        }
    }

    /**
     * Builds actions and new state table for the parser. Most vital method of
     * grammar.
     * 
     * @param dfa underlying dfa that is used to create tables
     */
    private void buildParserTable(DFAExtended<LREntry, Symbol> dfa) {

        Map<Integer, Set<LREntry>> aliases = dfa.getAliases();
        Map<Integer, Map<Symbol, Integer>> transitions = dfa.getDfa().getTransitions();

        List<Integer> states = new ArrayList<>(aliases.keySet());
        Collections.sort(states);

        for (Integer state : transitions.keySet()) {
            Map<Symbol, LRAction> actions4State = new HashMap<>();
            Map<Symbol, Integer> newStates4State = new HashMap<>();

            Map<Symbol, Integer> trans = transitions.get(state);
            List<LREntry> entries = new ArrayList<>(aliases.get(state));
            Collections.sort(entries);

            boolean complete = false;

            for (LREntry entry : entries) {
                if (entry.isComplete()) {
                    complete = true;
                }

                if (complete) {
                    if (entry.getProduction().getLHS().equals(startingProduction.getLHS())) {
                        for (Symbol a : fromBitSet(entry.getStartSet())) {
                            // should be only one symbol, end_stream
                            actions4State.put(a, new AcceptAction());
                        }
                    }
                    for (Symbol a : fromBitSet(entry.getStartSet())) {
                        if (!actions4State.containsKey(a)) {
                            // reduce/move contradiction
                            Production p = entry.getProduction();
                            actions4State.put(a, new ReduceAction(p));
                        }
                    }

                } else {
                    Symbol a = entry.getTransitionSymbol();
                    Integer nextState = trans.get(a);
                    if (!a.isTerminal()) {
                        if (!newStates4State.containsKey(a)) {
                            newStates4State.put(a, nextState);
                        }
                    } else {
                        if (nextState != null) {
                            // move/move contradiction, only first
                            if (!actions4State.containsKey(a)) {
                                actions4State.put(a, new MoveAction(nextState));
                            }
                        }
                    }
                }
            }

            actions.put(state, actions4State);
            newStates.put(state, newStates4State);
        }

    }

    private List<Symbol> fromBitSet(BitSet bs) {
        List<Symbol> ls = new LinkedList<>();
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
            ls.add(rmapper.get(i + T));
        }
        return ls;
    }

    /**
     * Creates a dfa from this grammar.
     * 
     * @return dfa
     */
    private DFAExtended<LREntry, Symbol> generateDFA() {

        /*
         * * * * * * * * * * ENFA parameters * * * * * * * * *
         */

        // S' -> .S, { STREAM_END } is the start state
        // S' -> S., { STREAM_END } is the final state
        // starts(S') = { STREAM_END }
        BitSet startingStateStartSet = new BitSet(starts.length);
        startingStateStartSet.set(mapper.get(Symbol.STREAM_END) - T);
        LREntry startState = new LREntry(startingProduction, startingStateStartSet);
        LREntry finalState = startState.next();

        // empty maps for transitions
        Map<LREntry, Map<Symbol, Set<LREntry>>> transitions = new HashMap<>();
        Map<LREntry, Set<LREntry>> epsilonTransitions = new HashMap<>();

        // create empty ENFA with no transitions
        EpsilonNFA<LREntry, Symbol> enfa = new EpsilonNFA<LREntry, Symbol>(startState, finalState,
                transitions, epsilonTransitions);

        Queue<LREntry> queue = new LinkedList<>();
        Set<LREntry> enqueued = new HashSet<>();
        queue.add(startState);
        enqueued.add(startState);

        /*
         * * * * * * * * * Add transitions to ENFA * * * * * * * * *
         */

        while (!queue.isEmpty()) {
            LREntry entry = queue.poll();
            while (!entry.isComplete()) {

                // case 4b
                Symbol sym = entry.getTransitionSymbol();
                LREntry next = entry.next();
                enfa.addTransition(entry, sym, next);

                // case 4c
                if (!sym.isTerminal()) {
                    // entry = A -> x.By, {a1,...,an}
                    // next  = A -> xB.y, {a1,...,an}

                    // case 4 - c - i
                    if (next.isComplete()) {

                    }
                    BitSet newStartSet = next.getStartSetFromDot();
                    if (next.isEmptyAfterDot()) {
                        // case 4 - c - ii
                        newStartSet.or(entry.getStartSet());
                    }

                    for (Production ntp : productions.get(sym)) {
                        LREntry nonTermEntry = new LREntry(ntp, newStartSet);
                        enfa.addEpsilonTransition(entry, nonTermEntry);

                        // if not already processed
                        if (!enqueued.contains(nonTermEntry)) {
                            queue.add(nonTermEntry);
                            enqueued.add(nonTermEntry);
                        }
                    }
                }
                entry = next;
            }
        }

        DFAExtended<LREntry, Symbol> dfa = enfa.toNFA().toDFA();
        return dfa;
    }

    /**
     * Initializes mappers for symbols. Maps from symbol to integer (alias) and
     * from integer to symbol.
     */
    private void initMappers() {
        mapper = new HashMap<>();
        rmapper = new HashMap<>();
        int idx = 0;
        for (Symbol s : nonTerminalSymbols) {
            rmapper.put(idx, s);
            mapper.put(s, idx++);
        }

        for (Symbol s : terminalSymbols) {
            rmapper.put(idx, s);
            mapper.put(s, idx++);
        }
    }

    /**
     * Initializes all possible start sets. For symbols and for productions.
     */
    private void initStartSets() {
        T = nonTerminalSymbols.size();
        int V = terminalSymbols.size();
        int N = V + T;

        // initial configuration
        starts = new BitSet[N];
        for (int t = 0; t < T; t++) {
            starts[t] = new BitSet(V);
        }
        for (int v = T; v < N; v++) {
            starts[v] = new BitSet(V);
            starts[v].set(v - T);
        }
        starts[mapper.get(Symbol.START_SYMBOL)].set(mapper.get(Symbol.STREAM_END) - T);

        // initializing helper map for configuring start sets
        Map<Integer, Set<Integer>> helper = new HashMap<>();
        for (Symbol nts : nonTerminalSymbols) {
            int idx1 = mapper.get(nts);
            Set<Integer> forIdx1 = new HashSet<>();

            for (Production p : productions.get(nts)) {
                if (p.isEpsilonProduction()) {
                    continue;
                }

                for (int i = 0; i < p.getSize(); i++) {
                    Symbol empty = p.getAt(i);
                    int idx2 = mapper.get(empty);

                    if (empty.isTerminal()) {
                        starts[idx1].set(idx2 - T);
                    } else {
                        forIdx1.add(idx2);
                    }

                    if (!empty.isEmpty()) {
                        break;
                    }
                }
            }
            helper.put(idx1, forIdx1);
        }

        // equivalence relation
        boolean change;
        do {
            change = false;
            for (int t = 0; t < T; t++) {
                Set<Integer> neigh = helper.get(t);
                Set<Integer> tmp = new HashSet<>(neigh);

                for (Integer x : neigh) {
                    change |= tmp.addAll(helper.get(x));
                }
                helper.put(t, tmp);
            }
        } while (change);

        // creating final start sets
        for (int t = 0; t < T; t++) {
            for (Integer t2 : helper.get(t)) {
                starts[t].or(starts[t2]);
            }
        }

        // caching start sets to productions
        for (List<Production> ps : productions.values()) {
            for (Production p : ps) {
                p.annotate(starts, mapper);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol key : productions.keySet()) {
            sb.append(key);
            sb.append('\n');
            for (Production p : productions.get(key)) {
                sb.append(' ');
                sb.append(p.toString());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

}