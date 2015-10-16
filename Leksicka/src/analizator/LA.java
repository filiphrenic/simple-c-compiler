package analizator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.automaton.AutomatonHandler;
import hr.fer.zemris.ppj.lex.Lex;
import hr.fer.zemris.ppj.lex.LexRule;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * This class reads the automatons from a file, creates a lexical analyzer based
 * on those automatons and analyzes the input
 * 
 * @author fhrenic
 */
public class LA {

    public static void main(String[] args) {
        // TODO
        // create input & output streams, create LA object and perform analysis

    }

    private InputStream input;
    private OutputStream output;

    /**
     * Creates a new Lexical Analyzer that analyzes the input stream and prints
     * the results to the output stream
     * 
     * @param input input stream to analyze
     * @param output output stream to show the results
     */
    public LA(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Performs lexical analysis of the input stream.
     */
    @SuppressWarnings("unchecked")
    public void lexicalAnalysis() {
        try (ObjectInputStream stream = Streamer.getInput()) {
            String startState = (String) stream.readObject();
            HashMap<String, List<LexRule>> states = (HashMap<String, List<LexRule>>) stream
                    .readObject();
            AutomatonHandler handler = (AutomatonHandler) stream.readObject();
            Lex lex = new Lex(startState, states, handler, input, output);
            lex.analyzeInput();
        } catch (IOException | ClassNotFoundException ex) {
            // TODO: handle exception
        }
    }

}
