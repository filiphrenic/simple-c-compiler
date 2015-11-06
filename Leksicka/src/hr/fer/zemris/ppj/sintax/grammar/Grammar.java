package hr.fer.zemris.ppj.sintax.grammar;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // zadnja metoda u konstruktoru
        generateDFA();
    }

    public Production getStartingProduction() {
        return startingProduction;
    }

    public List<Symbol> starts(Symbol sym) {
        // zapocinje znakom
        // zavrsni zavrsava jedino sam sa sobom
        // nezavrsni zavrsava s refleksivnim tranzitivnim okruzenjem...
        return null;
    }

    public List<Symbol> follows(Symbol sym) {
        // SLIJEDI(x) = skup svih zavrsnih
        return null;
    }

    public List<Production> getProductionsForLhs(Symbol sym) {
        // vraca sve produkcije za neki nezavrsni znak
        // tipa za A vraca produkcije A->a; A->bBaA; ...
        return null;
    }

    public List<Production> getProductions() {
        List<Production> productions = Collections.singletonList(startingProduction);
        for (List<Production> lps : this.productions.values()) {
            productions.addAll(lps);
        }
        return productions;
    }

    private void generateDFA() {

        // enfa parameters
        LREntry startState = new LREntry(this, getStartingProduction());
        LREntry finalState = startState.next();
        Map<LREntry, Map<Symbol, Set<LREntry>>> transitions = new HashMap<>();
        Map<LREntry, Set<LREntry>> epsilonTransitions = new HashMap<>();
        // empty enfa
        EpsilonNFA<LREntry, Symbol> enfa = new EpsilonNFA<LREntry, Symbol>(startState, finalState,
                transitions, epsilonTransitions);

        // add states and transitions
        for (Production p : getProductions()) {
            LREntry entry = new LREntry(this, p);

            while (!entry.isComplete()) {
                LREntry next = entry.next();
                Symbol sym = entry.getTransitionSymbol();
                enfa.addTransition(entry, sym, next);

                if (sym.getType() == SymbolType.NON_TERMINAL) {
                    for (Production ntp : getProductionsForLhs(sym)) {
                        enfa.addEpsilonTransition(entry, new LREntry(this, ntp));
                    }
                }
            }
        }

        dfa = enfa.toNFA().toDFA();
    }

}
