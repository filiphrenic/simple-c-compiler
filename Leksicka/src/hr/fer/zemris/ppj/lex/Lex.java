package hr.fer.zemris.ppj.lex;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class Lex {

    private String startState;
    private HashMap<String, List<LexRule>> states;
    private InputStream input;
    private OutputStream output;

    public Lex(String startState, HashMap<String, List<LexRule>> states, InputStream input,
            OutputStream output) {
        this.startState = startState;
        this.states = states;
        this.input = input;
        this.output = output;
    }

    public void analyzeInput() {
        // we should maybe save the table that we need to print out
        // maybe it will be needed in later exercises so we won't 
        // have to parse it
    }

}
