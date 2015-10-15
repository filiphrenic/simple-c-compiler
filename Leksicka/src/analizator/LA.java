/**
 * 
 */
package analizator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.lex.Lex;
import hr.fer.zemris.ppj.lex.LexRule;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * @author fhrenic
 */
public class LA {

    public static void main(String[] args) {
        // TODO
    }

    private InputStream input;
    private OutputStream output;

    public LA(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void lexicalAnalysis() {
        try (ObjectInputStream stream = Streamer.getInput()) {
            String startState = (String) stream.readObject();
            HashMap<String, List<LexRule>> states = (HashMap<String, List<LexRule>>) stream
                    .readObject();
            Lex lex = new Lex(startState, states, input, output);
            lex.analyzeInput();
        } catch (IOException | ClassNotFoundException ex) {
            // TODO: handle exception
        }
    }

}
