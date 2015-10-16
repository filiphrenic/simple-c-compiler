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
 */
public class GLA {

    public static void main(String[] args) {
    	
    	System.out.println("Alo!!!");
        // TODO
    }

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

    public void generateLA() {
        InputParser parser = new InputParser(input);
        try (ObjectOutputStream stream = Streamer.getOutput()) {
            stream.writeObject(parser.getStartState());
            stream.writeObject(parser.getStates());
        } catch (IOException ioe) {
            // TODO: handle exception
        }

    }

}
