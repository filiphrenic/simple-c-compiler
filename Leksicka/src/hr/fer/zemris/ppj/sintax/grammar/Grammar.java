package hr.fer.zemris.ppj.sintax.grammar;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

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
    private Map<Symbol, List<Production>> productions;
    private Production startingProduction; // S' -> S
    
    private Map<Symbol, Set<Symbol>> firstSets;
//    private Map<Symbol, Set<Symbol>> followSets;

    private DFAExtended<LREntry, Symbol> dfa;

    public Grammar(List<Symbol> terminalSymbols, Map<Symbol, List<Production>> productions,
            Production startingProduction) {
        this.terminalSymbols = terminalSymbols;
        this.productions = productions;
        this.startingProduction = startingProduction;

        // findEmptySymbols();
        // nađi slijedi i zapocinje okruzenja

        // finds empty symbols
        annotateEmptySymbols();
        
        constructFirstSets();
//        followSets = new FollowRelation(this).getFollowSets();

        for (Symbol s : terminalSymbols) {
            System.out.println(s + " " + s.isEmpty());
        }

        // zadnja metoda u konstruktoru
        //generateDFA();
    }
    
    public List<Symbol> getTerminalSymbols() {
    	return terminalSymbols;
    }
    
    public Set<Symbol> getNonTerminalSymbols() {
    	return productions.keySet();
    }
    
//    public Map<Symbol, List<Production>> getAllProductions() {
//    	return productions;
//    }

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

    public Production getStartingProduction() {
        return startingProduction;
    }

    private Set<Symbol> startsWith(Symbol sym) {
        // zapocinje znakom
        // zavrsni zavrsava jedino sam sa sobom
        // nezavrsni zavrsava s refleksivnim tranzitivnim okruzenjem...
        return firstSets.get(sym);
    }

    private Set<Symbol> follows(Symbol sym) {
        // SLIJEDI(x) = skup svih zavrsnih
//        return followSets.get(sym);
    	return null;
    }

    public List<Production> getProductionsForLhs(Symbol sym) {
        // vraca sve produkcije za neki nezavrsni znak
        // tipa za A vraca produkcije A->a; A->bBaA; ...
        return productions.get(sym);
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
    
    private void constructFirstSets() {
        firstSets = new HashMap<Symbol, Set<Symbol>>();
        // za svaki simbol nadji set simbola ZAPOCINJE(simbol)

        // izravno zapocinje
        for(Symbol symbol : terminalSymbols) {
        	Set<Symbol> firstSet = new HashSet<Symbol>();
        	firstSet.add(symbol);
        	firstSets.put(symbol, firstSet);
        }
        
        Set<Symbol> nonTerminalSymbols = productions.keySet();
        for(Symbol symbol : nonTerminalSymbols) {
        	Set<Symbol> firstSet = new HashSet<Symbol>();
            
        	for(Production production : productions.get(symbol)) {
            	for(Symbol rhsSym : production.getRHS()) {
            		firstSet.add(rhsSym);
            		if(!rhsSym.isEmpty()) {
            			break;
            		}
            	}	
            }

    		firstSets.put(symbol, firstSet);
    	}

    	// ref. tran. okruzenje
        boolean change;
        do {
        	change = false;
	        for(Symbol symbol : nonTerminalSymbols) {
	    		Set<Symbol> firstSet = firstSets.get(symbol);
	    		Set<Symbol> tmp = new HashSet<Symbol>(firstSet);
	    		for(Symbol first : firstSet) {
	    			if(first.getType() == SymbolType.NON_TERMINAL) {
	    				if(tmp.addAll(firstSets.get(first))) {
	    					change = true;
	    				}
	    			}
	    		}
	    		firstSets.put(symbol, tmp);
	    	}
        } while(change);

    	// maknuti nezavrsne (i eps)
        for(Symbol symbol : nonTerminalSymbols) {
        	for(Iterator<Symbol> it = firstSets.get(symbol).iterator(); it.hasNext();) {
        		Symbol sym = it.next();
        		if(sym.getType() == SymbolType.NON_TERMINAL || "$".equals(sym.toString())) {
        			it.remove();
        		}
        	}
        }
    	
//    	System.out.println("---\nProvjera ZAPOCINJE skupova:");
//    	for(Symbol symbol : nonTerminalSymbols) {
//    		System.out.print(symbol + " -> ");
//    		for(Symbol sym : firstSets.get(symbol)) {
//    			System.out.print(sym + " ");
//    		}
//    		System.out.println();
//    	}
//    	System.out.println("kraj\n---");
    }

}
