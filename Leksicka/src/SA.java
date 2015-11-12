import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * @author fhrenic
 */
public class SA {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File("files/syntax/sa.in"));
        //InputStream input = System.in;
        new SA(input, System.out).sintaxAnalysis();
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
     * Performs sintax analysis of the input stream.
     */
    @SuppressWarnings("unchecked")
    public void sintaxAnalysis() {
        String fileName = Streamer.FOLDER + "/" + Streamer.SINTAX_OBJECTS;
        //String fileName = Streamer.SINTAX_OBJECTS;

        try (ObjectInputStream stream = Streamer.getInput(fileName)) {
            Map<Integer, Map<Symbol, LRAction>> actions = (Map<Integer, Map<Symbol, LRAction>>) stream
                    .readObject();
            Map<Integer, Map<Symbol, Integer>> newStates = (Map<Integer, Map<Symbol, Integer>>) stream
                    .readObject();
            // create parser with given actions, input and output stream
            new LRParser(input, output, actions, newStates).parse();
        } catch (IOException | ClassNotFoundException ex) {
            // TODO: handle exception
            System.out.println(ex);
        }
    }

}
