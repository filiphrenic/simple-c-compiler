
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.syntax.grammar.Grammar;
import hr.fer.zemris.ppj.util.Streamer;
import hr.fer.zemris.ppj.util.input.SyntaxInputParser;

/**
 * This class is used to generate a syntax analyzer. It reads in grammar
 * definition and based on that generates lr(1) parser's tables.
 * 
 * @author fhrenic
 */
public class GSA {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream input = new FileInputStream("tests_syntax/kanonska/test.san");
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

        Grammar grammar = sip.getConstructedGrammar();
        String filename = Streamer.getFilename4Generator(Streamer.SYNTAX_OBJECTS);

        try (ObjectOutputStream stream = Streamer.getOutput(filename)) {
            stream.writeObject(grammar.getActions());
            stream.writeObject(grammar.getNewStates());
            stream.writeObject(grammar.getSyncSymbols());

        } catch (IOException ioe) {
            System.err.println("Error in GSA: " + ioe.getMessage());
        }
    }

}
