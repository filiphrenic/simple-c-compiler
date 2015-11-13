import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.stream.LexicalInputParser;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * This class is used to generate a lexical analyzer. It reads in definitions,
 * creates automatons and writes them to the file that will be read by the
 * lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class GLA {

    /**
     * Method which is called when program starts.
     * 
     * @param args not used
     * @throws FileNotFoundException
     */
    public static void main(String[] args) {
        InputStream input = System.in;
        GLA generator = new GLA(input);
        generator.generateLA();
    }

    /**
     * Source for input: it can be FileInputStream, System.in, ...
     */
    private InputStream input;

    /**
     * Creates a new GLA (generator) whose job is to generate objects that will
     * be used by the LA (lexical analyzer)
     * 
     * @param input input stream used to read in the definitions
     */
    public GLA(InputStream input) {
        this.input = input;
    }

    /**
     * Generates objects needed by the lexical analyzer.
     */
    public void generateLA() {
        LexicalInputParser parser = new LexicalInputParser(input);
        String fileName = Streamer.FOLDER + "/" + Streamer.LEXICAL_OBJECTS;

        try (ObjectOutputStream stream = Streamer.getOutput(fileName)) {
            stream.writeObject(parser.getStartState());
            stream.writeObject(parser.getStates());
        } catch (IOException ioe) {
            System.err.println("Error in GLA: " + ioe.getMessage());
        }

    }

}
