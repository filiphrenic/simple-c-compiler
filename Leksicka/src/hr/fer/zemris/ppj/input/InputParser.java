package hr.fer.zemris.ppj.input;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.lex.LexRule;

/**
 * 
 * @author fhrenic
 */
public class InputParser {

    private InputStream input;

    public InputParser(InputStream input) {
        this.input = input;
    }

    /*
     * For regular definitions, only call
     * new Automaton( regex, regDefName ); 
     * so regdef will be in the table
     */

    public List<String> getStates() {
        return null;
    }

    public List<String> getLexClasses() {
        return null;
    }

    public HashMap<String, List<LexRule>> getRules() {
        return null;
    }
}
