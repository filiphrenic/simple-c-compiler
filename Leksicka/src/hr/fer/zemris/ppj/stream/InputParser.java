package hr.fer.zemris.ppj.stream;

import hr.fer.zemris.ppj.lex.LexRule;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class which reads definitions for generator of lexical analyzer and offers getters for states,
 * lexical classes and rules for lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class InputParser {

    private InputStream input;
    private List<String> states;
    private List<String> lexClasses;
    private HashMap<String, List<LexRule>> rules;

    /**
     * Creates new instance of {@link InputParser} which can read given input. 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public InputParser(InputStream input) {
        this.input = input;
    }

    /**
     * 
     */
    public void parse() {
    	
    }

    /*
     * For regular definitions, only call
     * new Automaton( regex, regDefName ); 
     * so regdef will be in the table
     */

    /**
     * Returns a list of lexical analyzer states.
     * @return list of states of lexical analyzer 
     */
    public List<String> getStates() {
        return states;
    }

    /**
     * Returns start state of lexical analyzer if such one exists.
     * @return start state of lexical analyzer
     * @throws NoSuchElementException if there is no start state, ie. no states at all.
     */
    public String getStartState() {
        if (states.isEmpty()) {
        	throw new NoSuchElementException("There is no start state.");
        } 
        
        return states.get(0);
    }

    /**
     * Returns a list of lexical classes.
     * @return list of lexical classes
     */
    public List<String> getLexClasses() {
        return lexClasses;
    }

    /**
     * Returns map of rules for lexical analyzer in order as they are found in input file.
     * @return map of rules for lexical analyzer
     */
    public HashMap<String, List<LexRule>> getRules() {
        return rules;
    }
}
