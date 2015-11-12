
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ppj.sintax.grammar.Grammar;
import hr.fer.zemris.ppj.stream.SintaxInputParser;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * 
 * @author ajuric
 * @author fhrenic 
 */
public class GSA {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File("grah.txt"));
        //InputStream input = System.in;
        GSA generator = new GSA(input);
        generator.generateSA();
    }

    private InputStream input;

    public GSA(InputStream input) {
        this.input = input;
    }

    public void generateSA() {
        SintaxInputParser sip = new SintaxInputParser(input);
        Grammar g = sip.getConstructedGrammar();
        String fileName = Streamer.FOLDER + "/" + Streamer.SINTAX_OBJECTS;

        try (ObjectOutputStream stream = Streamer.getOutput(fileName)) {
            stream.writeObject(g.getActions());
        } catch (IOException ioe) {
            // TODO: handle exception
            System.err.println("Error in GSA: " + ioe.getMessage());
        }
    }

}
