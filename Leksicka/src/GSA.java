
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.stream.Streamer;
import hr.fer.zemris.ppj.stream.SyntaxInputParser;
import hr.fer.zemris.ppj.syntax.grammar.Grammar;

/**
 * This class is used to generate a sytax analyzer. It reads in grammar
 * definition and based on that generates lr(1) parser's tables.
 * 
 * @author fhrenic
 */
public class GSA {

    public static void main(String[] args) {
        InputStream input = System.in;
        GSA generator = new GSA(input);
        generator.generateSA();
    }

    /**
     * Input stream used to read in grammar definition.
     */
    private InputStream input;

    /**
     * Creates a new generator that will use given input stream.
     * 
     * @param input input stream that will be used to read grammar definition
     */
    public GSA(InputStream input) {
        this.input = input;
    }

    /**
     * Generates tables for LR parser in such way that it writes them out to a
     * file. More concretely, tables are actions and new state.
     */
    public void generateSA() {
        SyntaxInputParser sip = new SyntaxInputParser(input);
        Grammar g = sip.getConstructedGrammar();
        String filename = Streamer.getFilename4Generator(Streamer.SYNTAX_OBJECTS);

        try (ObjectOutputStream stream = Streamer.getOutput(filename)) {
            stream.writeObject(g.getActions());
            stream.writeObject(g.getNewStates());
        } catch (IOException ioe) {
            System.err.println("Error in GSA: " + ioe.getMessage());
        }
    }

}
