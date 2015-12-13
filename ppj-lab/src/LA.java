import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.lexical.Lex;
import hr.fer.zemris.ppj.lexical.LexRule;
import hr.fer.zemris.ppj.util.Streamer;

/**
 * This class reads the automatons from a file, creates a lexical analyzer based
 * on those automatons and analyzes the input
 * 
 * @author fhrenic
 */
public class LA {

    public static void main(String[] args) {
        InputStream input = System.in;
        new LA(input, System.out).lexicalAnalysis();
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
        String filename = Streamer.getFilename4Analyzer(Streamer.LEXICAL_OBJECTS);

        try (ObjectInputStream stream = Streamer.getInput(filename)) {
            String startState = (String) stream.readObject();
            HashMap<String, List<LexRule>> states = (HashMap<String, List<LexRule>>) stream
                    .readObject();
            new Lex(startState, states, output).analyzeInput(input);
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error in LA: " + ex.getMessage());
        }
    }

}
