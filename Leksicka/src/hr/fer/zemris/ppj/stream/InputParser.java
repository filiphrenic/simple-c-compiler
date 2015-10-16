package hr.fer.zemris.ppj.stream;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.automaton.AutomatonHandler;
import hr.fer.zemris.ppj.lex.LexRule;
import hr.fer.zemris.ppj.lex.actions.IAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private AutomatonHandler handler;

    /**
     * Creates new instance of {@link InputParser} which reads given input and parses it. 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public InputParser(InputStream input) {
        this.input = input;
        handler = new AutomatonHandler();
        states = new ArrayList<>();
        lexClasses = new ArrayList<String>();
        rules = new LinkedHashMap<>();
        try {
			parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
     */

    /**
     * Parses definitions for generator of lexical analyzer. 
     * @throws IOException 
     */
    private void parse() throws IOException {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	String line = null;
    	
    	readRegDef(reader, line);
    	readStates(reader, line);
    	readLexClasses(reader, line);
    	readRules(reader, line);
    	
    	reader.close();
    }

    /**
     * Reads regular definitions from input.
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
    private void readRegDef(BufferedReader reader, String line) throws IOException {
    	while (true) {
    		line = reader.readLine();
    		if (line.startsWith("%X")) {
    			break;
    		}
    		
    		String[] regDefs = line.split("\\s");
    		String regDefName = regDefs[0];
    		regDefName = regDefName.substring(1, regDefName.length() - 1);
    		String regEx = regDefs[1];
    		handler.fromString(regEx, regDefName);
    	}
	}

    /**
     * Reads states from input.
     * @param reader for 
     * @param line read line
     */
	private void readStates(BufferedReader reader, String line) {
    	line = line.substring(3);
    	String[] statesArray = line.split("\\s");
    	for (String state : statesArray) {
    		states.add(state);
    	}
	}

	/**
	 * Reads lexical classes from input.
	 * @param reader for input
	 * @param line read line
	 * @throws IOException if error occurs while reading
	 */
    private void readLexClasses(BufferedReader reader, String line) throws IOException {
    	line = reader.readLine();
    	line = line.substring(3);
    	String[] lexClassesArray = line.split("\\s");
    	for (String lexClass : lexClassesArray) {
    		lexClasses.add(lexClass);
    	}
	}

    /**
     * Reads rules from input.
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
	private void readRules(BufferedReader reader, String line) throws IOException {
    	while(line != null) {
    		line = reader.readLine();
    		String state = line.substring(1, line.indexOf(">"));
    		String regEx = line.substring(line.indexOf(">") + 1);
    		line = reader.readLine();
    		Automaton automaton = handler.fromString(regEx, null);
    		List<IAction> actions = new ArrayList<>();
    		while (!line.equals("}")) {
    			line = reader.readLine();
    			actions.add(createAction());
    		}
    		List<LexRule> lexRules = rules.get(state);
    		if (lexRules == null) {
    			lexRules = new ArrayList<>();
    		}
    		lexRules.add(new LexRule(automaton, actions));
    		rules.put(state, lexRules);
    	}
	}

	private IAction createAction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gives the {@link AutomatonHandler}.
	 * @return automaton handler
	 */
	public AutomatonHandler getAutomatonHandler() {
        return handler;
    }

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
