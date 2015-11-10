package hr.fer.zemris.ppj.stream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.ppj.sintax.grammar.Grammar;
import hr.fer.zemris.ppj.sintax.grammar.Production;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;
import hr.fer.zemris.ppj.sintax.grammar.SymbolType;

/**
 * Class which reads definitions for generator of lexical analyzer and offers
 * getters for states, lexical classes and rules for lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class SintaxInputParser {

    private static final String EPS_SYMBOL = "$";
    private static final String START_SYMBOL = "<s_crtano>";

    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream("gramatika100.san");
        SintaxInputParser sip = new SintaxInputParser(input);
        Grammar g = sip.getConstructedGrammar();
        System.out.println(g);
    }

    private Map<String, Symbol> terminalSymbols;
    private Map<String, Symbol> nonTerminalSymbols;
    private Map<Symbol, List<Production>> productions;
    private Production startingProduction;
    private String startState;

    private String currLine;

    private Grammar g;

    /**
     * Creates new instance of {@link SintaxInputParser} which reads given input and
     * parses it.
     * 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public SintaxInputParser(InputStream input) {
        terminalSymbols = new HashMap<>();
        nonTerminalSymbols = new HashMap<>();
        productions = new LinkedHashMap<>();

        // is symbol $ printed? set false if not
        Symbol eps = new Symbol(SymbolType.TERMINAL, EPS_SYMBOL, true);
        eps.setEmpty(true);
        terminalSymbols.put(EPS_SYMBOL, eps);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            readGrammar(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Symbol> terminals = new ArrayList<>(terminalSymbols.values());

        g = new Grammar(terminals, productions, startingProduction);
    }

    public Grammar getConstructedGrammar() {
        return g;
    }

    /**
     * Parses definitions for generator of lexical analyzer.
     * 
     * @throws IOException
     */
    private void readGrammar(BufferedReader reader) throws IOException {

        // read non terminal
        currLine = reader.readLine();
        currLine = currLine.substring(currLine.indexOf(' ') + 1);
        // set start state
        int nextSpace = currLine.indexOf(' ');
        if (nextSpace == -1) {
            nextSpace = currLine.length();
        }
        startState = currLine.substring(0, nextSpace);
        readSymbols(currLine, true);

        // read terminal
        currLine = reader.readLine();
        currLine = currLine.substring(currLine.indexOf(' '));
        readSymbols(currLine, true);

        // read sync
        currLine = reader.readLine();
        for (Symbol sync : readSymbols(currLine.substring(currLine.indexOf(' ')), false)) {
            sync.setSync(true);
        }

        Symbol startSymbol = readSymbol(START_SYMBOL, true);
        nonTerminalSymbols.put(START_SYMBOL, startSymbol);

        Symbol realStartSymbol = nonTerminalSymbols.get(startState);
        startingProduction = new Production(startSymbol,
                Collections.singletonList(realStartSymbol));
        productions.put(startSymbol, Collections.singletonList(startingProduction));

        readAllProductions(reader);
    }

    private void readAllProductions(BufferedReader reader) throws IOException {
        currLine = reader.readLine();
        while (currLine != null) {
            Symbol lhs = readSymbol(currLine, false);
            List<Production> ps = productions.get(lhs);
            if (ps == null) {
                ps = new LinkedList<>();
                productions.put(lhs, ps);
            }
            ps.addAll(readProductions(reader, lhs));
        }
    }

    private List<Production> readProductions(BufferedReader reader, Symbol lhs) throws IOException {
        List<Production> productions = new LinkedList<>();
        while (true) {
            currLine = reader.readLine();
            if (currLine == null || !currLine.startsWith(" ")) {
                break;
            }
            productions.add(readProduction(lhs, currLine));
        }
        return productions;
    }

    private Production readProduction(Symbol lhs, String line) {
        Production p = new Production(lhs, readSymbols(line, false));
        return p;
    }

    private List<Symbol> readSymbols(String s, boolean store) {
        List<Symbol> symbols = new ArrayList<>();
        String[] names = s.trim().split(" ");
        for (String name : names) {
            symbols.add(readSymbol(name, store));
        }
        return symbols;
    }

    private Symbol readSymbol(String name, boolean store) {
        Map<String, Symbol> map = terminalSymbols;
        SymbolType type = SymbolType.TERMINAL;
        if (name.startsWith("<")) {
            //name = name.substring(1, name.length() - 1);
            map = nonTerminalSymbols;
            type = SymbolType.NON_TERMINAL;
        }

        Symbol sym = map.get(name);
        if (store) {
            sym = new Symbol(type, name, true);
            map.put(name, sym);
        }

        return sym;
    }

}
