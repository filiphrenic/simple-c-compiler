import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Map;

import hr.fer.zemris.ppj.sintax.actions.LRAction;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * @author fhrenic
 */
public class SA {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File("primjer.minus.txt"));
        //InputStream input = System.in;
        //new LA(input, System.out).lexicalAnalysis();
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
     * Performs lexical analysis of the input stream.
     */
    @SuppressWarnings("unchecked")
    public void lexicalAnalysis() {
        String fileName = Streamer.SINTAX_OBJECTS;

        try (ObjectInputStream stream = Streamer.getInput(fileName)) {
            Map<Integer, Map<Symbol, LRAction>> actions = (Map<Integer, Map<Symbol, LRAction>>) stream
                    .readObject();
            // create parser with given actions, input and output stream
            // new LRParser(input, output, actions).parse();
        } catch (IOException | ClassNotFoundException ex) {
            // TODO: handle exception
        }
    }

}
