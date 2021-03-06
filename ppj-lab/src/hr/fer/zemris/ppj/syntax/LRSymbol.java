package hr.fer.zemris.ppj.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * A wrapper around symbol that also has line number and original text.
 * 
 * @author fhrenic
 */
public class LRSymbol {

    /**
     * Read symbols from input stream
     * 
     * @param input stream
     * @return list of read symbols
     */
    public static List<LRSymbol> readSymbolsFrom(InputStream input, List<String> syncSymbols) {
        List<LRSymbol> symbols = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String currLine;
            while ((currLine = reader.readLine()) != null) {
                String[] elements = currLine.split(" ", 3);
                Symbol sym = new Symbol(elements[0], true);
                if (syncSymbols.contains(sym.toString())) {
                    sym.setSync(true);
                }
                int lineNumber = Integer.parseInt(elements[1]);
                String originalText = elements[2];
                LRSymbol lrsym = new LRSymbol(sym, lineNumber, originalText);
                symbols.add(lrsym);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        symbols.add(new LRSymbol(Symbol.STREAM_END));

        return symbols;
    }

    private Symbol symbol;
    private int lineNumber;
    private String originalText;

    /**
     * Create a new {@link LRSymbol} with given properties.
     * 
     * @param symbol
     * @param lineNumber
     * @param originalText
     */
    public LRSymbol(Symbol symbol, int lineNumber, String originalText) {
        this.symbol = symbol;
        this.lineNumber = lineNumber;
        this.originalText = originalText;
    }

    /**
     * Create a new {@link LRSymbol} that doesn't need to have line number and
     * original text properties.
     * 
     * @param symbol
     */
    public LRSymbol(Symbol symbol) {
        this(symbol, -1, "");
    }

    /**
     * @return the symbol
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the originalText
     */
    public String getOriginalText() {
        return originalText;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LRSymbol)) {
            return false;
        }
        LRSymbol other = (LRSymbol) obj;
        return lineNumber == other.lineNumber && originalText == other.originalText
                && symbol.equals(other.symbol);
    }

    @Override
    public int hashCode() {
        int hash = lineNumber;
        hash = hash * 31 + symbol.hashCode();
        hash = hash * 31 + originalText.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        if (symbol.isTerminal() && symbol != Symbol.EPS_SYMBOL) {
            return symbol + " " + lineNumber + " " + originalText;
        } else {
            return symbol.toString();
        }
    }

}
