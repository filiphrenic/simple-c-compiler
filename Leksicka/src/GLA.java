import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.stream.InputParser;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * 
 */

/**
 * @author fhrenic
 */
public class GLA {

    public static void main(String[] args) {
        // TODO
    }

    private InputStream input;

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
