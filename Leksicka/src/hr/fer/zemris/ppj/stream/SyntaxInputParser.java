package hr.fer.zemris.ppj.stream;

import java.io.BufferedReader;
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

import hr.fer.zemris.ppj.syntax.grammar.Grammar;
import hr.fer.zemris.ppj.syntax.grammar.Production;
import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * Class which reads definitions for generator of lexical analyzer and offers
 * getters for states, lexical classes and rules for lexical analyzer.
 * 
 * @author fhrenic
 */
public class SyntaxInputParser {

    private Map<String, Symbol> terminalSymbols;
    private Map<String, Symbol> nonTerminalSymbols;
    private Map<Symbol, List<Production>> productions;
    private Production startingProduction;
    private String startState;

    private String currLine;

    private Grammar grammar;
    private int productionId;

    /**
     * Creates new instance of {@link SyntaxInputParser} which reads given input
     * and parses it.
     * 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public SyntaxInputParser(InputStream input) {
        terminalSymbols = new HashMap<>();
        nonTerminalSymbols = new HashMap<>();
        productions = new LinkedHashMap<>();

        terminalSymbols.put(Symbol.EPS_SYMBOL_NAME, Symbol.EPS_SYMBOL);
        nonTerminalSymbols.put(Symbol.START_SYMBOL_NAME, Symbol.START_SYMBOL);
        productionId = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            readGrammar(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Symbol> terminals = new ArrayList<>(terminalSymbols.values());
        terminals.add(Symbol.STREAM_END);
        List<Symbol> nonTerminals = new ArrayList<>(nonTerminalSymbols.values());

        grammar = new Grammar(terminals, nonTerminals, productions, startingProduction);
    }

    /**
     * Returns the grammar that was constructed from the grammar definition.
     * 
     * @return grammar
     */
    public Grammar getConstructedGrammar() {
        return grammar;
    }

    /**
     * Parses definitions of a grammar.
     * 
     * @param reader used for reading lines
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

        Symbol realStartSymbol = nonTerminalSymbols.get(startState);
        startingProduction = new Production(Symbol.START_SYMBOL,
                Collections.singletonList(realStartSymbol), productionId++);
        productions.put(Symbol.START_SYMBOL, Collections.singletonList(startingProduction));

        readAllProductions(reader);
    }

    /**
     * This method reads all of the productions and stores them in the map.
     * 
     * @param reader line reader
     * @throws IOException
     */
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

    /**
     * Reads production for a single non-terminal symbols. This method is called
     * multiple times in the readAllProductions method.
     * 
     * @param reader line reader
     * @param lhs left hand side of the production
     * @return list of productions for given lhs
     * @throws IOException
     */
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

    /**
     * Reads a single production (one line).
     * 
     * @param lhs left hand side of the production
     * @param line line to read production from
     * @return constructed production
     */
    private Production readProduction(Symbol lhs, String line) {
        Production p = new Production(lhs, readSymbols(line, false), productionId++);
        return p;
    }

    /**
     * Reads symbols from a string.
     * 
     * @param str string from which we read symbols
     * @param store indicates should the symbols be stored in the map (set
     *            <code>true</code> if uncertain)
     * @return list of symbols
     */
    private List<Symbol> readSymbols(String str, boolean store) {
        List<Symbol> symbols = new ArrayList<>();
        String[] names = str.trim().split(" ");
        for (String name : names) {
            symbols.add(readSymbol(name, store));
        }
        return symbols;
    }

    /**
     * Reads a single symbol.
     * 
     * @param name only thing that this string should contain is symbols name
     * @param store indicates should the symbols be stored in the map (set
     *            <code>true</code> if uncertain)
     * @return read symbol
     */
    private Symbol readSymbol(String name, boolean store) {
        Map<String, Symbol> map = terminalSymbols;
        boolean isTerminal = true;
        if (name.startsWith("<")) {
            //name = name.substring(1, name.length() - 1);
            map = nonTerminalSymbols;
            isTerminal = false;
        }

        Symbol sym = map.get(name);
        if (store || sym == null) {
            sym = new Symbol(name, isTerminal);
            map.put(name, sym);
        }

        return sym;
    }

}
