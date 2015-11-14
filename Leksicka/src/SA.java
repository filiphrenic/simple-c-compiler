import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Map;

import hr.fer.zemris.ppj.stream.Streamer;
import hr.fer.zemris.ppj.syntax.LRParser;
import hr.fer.zemris.ppj.syntax.actions.LRAction;
import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * This class is used to do syntax analysis of a program. It uses LR(1) parser.
 * 
 * @author fhrenic
 */
public class SA {
    public static void main(String[] args) {
        InputStream input = System.in;
        new SA(input, System.out).syntaxAnalysis();
    }

    private InputStream input;
    private OutputStream output;

    /**
     * Creates a new Sintax Analyzer that analyzes the input stream and prints
     * the results to the output stream
     * 
     * @param input input stream to analyze
     * @param output output stream to show the results
     */
    public SA(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Performs syntax analysis of the input stream.
     */
    @SuppressWarnings("unchecked")
    public void syntaxAnalysis() {
        String filename = Streamer.getFilename4Analyzer(Streamer.SYNTAX_OBJECTS);
        
        try (ObjectInputStream stream = Streamer.getInput(filename)) {
            Map<Integer, Map<Symbol, LRAction>> actions = (Map<Integer, Map<Symbol, LRAction>>) stream
                    .readObject();
            Map<Integer, Map<Symbol, Integer>> newStates = (Map<Integer, Map<Symbol, Integer>>) stream
                    .readObject();
            new LRParser(input, output, actions, newStates).parse();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error in SA: " + ex.getMessage());
        }
    }

}
