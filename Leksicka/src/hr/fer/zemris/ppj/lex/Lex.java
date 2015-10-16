package hr.fer.zemris.ppj.lex;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.automaton.AutomatonHandler;

/**
 * 
 * @author fhrenic
 */
public class Lex {

    private String startState;
    private HashMap<String, List<LexRule>> states;

    private int startIndex;
    private int endIndex;
    private int lastIndex;

    private int lineNumber;

    private InputStream input;
    private OutputStream output;

    public Lex(String startState, HashMap<String, List<LexRule>> states, AutomatonHandler handler,
            InputStream input, OutputStream output) {
        this.startState = startState;
        this.states = states;
        this.input = input;
        this.output = output;
        Automaton.setHandler(handler); // don't change this
    }

    public void analyzeInput() {
        // we should save the table that we need to print out
        // maybe it will be needed in later exercises so we won't 
        // have to parse it
    }

}
