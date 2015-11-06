package hr.fer.zemris.ppj.sintax.grammar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import hr.fer.zemris.ppj.automaton.DFAExtended;
import hr.fer.zemris.ppj.automaton.EpsilonNFA;
import hr.fer.zemris.ppj.sintax.LREntry;

/**
 * @author fhrenic
 */
public class Grammar {

    // ovu klasu treba napraviti statičkom!
    // to znači da se sve treba keširati, jer se budu metode pozivale jako često
    // ne smije se prilikom poziva get<nesto>() raditi nikakav posao, sve mora biti
    // staticki, minimalno posla

    // iznimka je metoda getProductions koja vraca sve produkcije, ona se bude pozvala
    // jednom za gradnju automata

    private List<Symbol> terminalSymbols;
    private List<Symbol> nonTerminalSymbols;
    private List<Symbol> syncSymbols;
    private Map<Symbol, List<Production>> productions;
    private Production startingProduction; // S' -> S

    private DFAExtended<LREntry, Symbol> dfa;

    public Grammar(List<Symbol> terminalSymbols, List<Symbol> nonTerminalSymbols,
            List<Symbol> syncSymbols, Map<Symbol, List<Production>> productions,
            Production startingProduction) {
        this.terminalSymbols = terminalSymbols;
        this.nonTerminalSymbols = nonTerminalSymbols;
        this.syncSymbols = syncSymbols;
        this.productions = productions;
        this.startingProduction = startingProduction;

        // findEmptySymbols();
        // nađi slijedi i zapocinje okruzenja

        // zadnja metoda u konstruktoru
        generateDFA();
    }

    private Production getStartingProduction() {
        return startingProduction;
    }

    private Set<Symbol> startsWith(Symbol sym) {
        // zapocinje znakom
        // zavrsni zavrsava jedino sam sa sobom
        // nezavrsni zavrsava s refleksivnim tranzitivnim okruzenjem...
        return null;
    }

    private Set<Symbol> follows(Symbol sym) {
        // SLIJEDI(x) = skup svih zavrsnih
        return null;
    }

    private List<Production> getProductionsForLhs(Symbol sym) {
        // vraca sve produkcije za neki nezavrsni znak
        // tipa za A vraca produkcije A->a; A->bBaA; ...
        return null;
    }

    private List<Production> getProductions() {
        List<Production> productions = Collections.singletonList(startingProduction);
        for (List<Production> lps : this.productions.values()) {
            productions.addAll(lps);
        }
        return productions;
    }

    /**
     * Returns the first non empty symbol in the production. For instance,
     * lets say we have a production S -> xABCD and that the from index = 1
     * (so we start looking from A). 
     * Let's also assume that A, B and D are empty symbols. 
     * So this method would return symbol C. 
     * 
     * @param p production to check
     * @param from index to check from
     * @return first non empty symbol, or <code>null</code> if none exists
     */
    private Symbol firstNonEmpty(Production p, int from) {
        int N = p.getRHS().size();
        for (int i = from; i < N; i++) {
            Symbol s = p.getRHS().get(i);
            if (s.isEmpty()) {
                return s;
            }
        }
        return null;
    }

    private void generateDFA() {

        /* * * * * * * * * *
         * ENFA parameters *
         * * * * * * * * * */

        // S' -> .S is the start state
        // S' -> S. is the final state
        // follows(S') = { STREAM_END }
        Set<Symbol> startStateFollowUps = Collections.singleton(Symbol.STREAM_END);
        LREntry startState = new LREntry(getStartingProduction(), startStateFollowUps);
        LREntry finalState = startState.next();

        // empty maps for transitions
        Map<LREntry, Map<Symbol, Set<LREntry>>> transitions = new HashMap<>();
        Map<LREntry, Set<LREntry>> epsilonTransitions = new HashMap<>();

        // create empty ENFA with no transitions
        EpsilonNFA<LREntry, Symbol> enfa = new EpsilonNFA<LREntry, Symbol>(startState, finalState,
                transitions, epsilonTransitions);

        /* * * * * * * * * * * * * *
         * Add transitions to ENFA *
         * * * * * * * * * * * * * */

        for (Symbol term : productions.keySet()) {
            Set<Symbol> followUps = follows(term);

            for (Production prod : productions.get(term)) {
                LREntry entry = new LREntry(prod, followUps);

                while (!entry.isComplete()) {
                    LREntry next = entry.next();
                    Symbol sym = entry.getTransitionSymbol();
                    enfa.addTransition(entry, sym, next);

                    if (sym.getType() == SymbolType.NON_TERMINAL) {

                        // TODO
                        // ovo se da ubrzati da ne trazi svaki put
                        Symbol nonEmpty = firstNonEmpty(prod, next.getDotIndex());
                        Set<Symbol> pFollowUps = new TreeSet<>();
                        if (nonEmpty != null) {
                            pFollowUps = follows(nonEmpty);
                        }
                        next.addFollowUps(pFollowUps);
                        for (Production ntp : getProductionsForLhs(sym)) {
                            enfa.addEpsilonTransition(entry, new LREntry(ntp, pFollowUps));
                        }

                    }

                }
            }

        }

        dfa = enfa.toNFA().toDFA();
    }

}
