package hr.fer.zemris.ppj.stream;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.automaton.AutomatonHandler;
import hr.fer.zemris.ppj.lex.LexRule;

/**
 * 
 * @author fhrenic
 */
public class InputParser {

    private InputStream input;
    private AutomatonHandler handler;

    public InputParser(InputStream input) {
        this.input = input;
        handler = new AutomatonHandler();
    }

    /*
     * To create an automate, call
     * Automate a = handler.fromString (regex, null)
     * 
     * 
     * For regular definitions, only call
     * handler.fromString( regex, regDefName ); 
     * so regdef will be in the table
     * 
     * to add a regex to an existing automate (only when parsing rules), call:
     * handler.addChoice( previousAutomaton, newAutomaton ) 
     */

    public AutomatonHandler getAutomatonHandler() {
        return handler;
    }

    public List<String> getStates() {
        return null;
    }

    public String getStartState() {
        return null;
    }

    public List<String> getLexClasses() {
        return null;
    }

    public HashMap<String, List<LexRule>> getRules() {
        return null;
    }
}
