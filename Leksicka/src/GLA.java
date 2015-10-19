import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.stream.InputParser;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * This class creates a generator, reads in definitions, creates automatons and
 * writes them to the file that will be read by the lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class GLA {

    /**
     * Method which is called when program starts.
     * 
     * @param args not used
     * @throws FileNotFoundException if input file is not found
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO change input stream
        //InputStream input = new FileInputStream("minusLang.lan");
        InputStream input = new FileInputStream("tests/regex_regdefs/test.lan");
        //InputStream input = System.in;
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
        InputParser parser = new InputParser(input);
        String result;
        try (ObjectOutputStream stream = Streamer.getOutput()) {
            stream.writeObject(parser.getStartState());
            stream.writeObject(parser.getStates());
            stream.writeObject(parser.getAutomatonHandler());
            result = "Generated tables";
        } catch (IOException ioe) {
            // TODO: handle exception
            result = "Error\n" + ioe.getMessage();
        }
        System.err.println(result);

    }

}
