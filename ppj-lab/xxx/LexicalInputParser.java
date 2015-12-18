package hr.fer.zemris.ppj.util.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.lexical.LexRule;
import hr.fer.zemris.ppj.lexical.actions.ChangeStateAction;
import hr.fer.zemris.ppj.lexical.actions.GoBackAction;
import hr.fer.zemris.ppj.lexical.actions.LexAction;
import hr.fer.zemris.ppj.lexical.actions.NewLineAction;
import hr.fer.zemris.ppj.lexical.actions.SkipAction;
import hr.fer.zemris.ppj.lexical.automaton.LexAutomaton;
import hr.fer.zemris.ppj.lexical.automaton.LexAutomatonHandler;

/**
 * Class which reads definitions for generator of lexical analyzer and offers
 * getters for states, lexical classes and rules for lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class LexicalInputParser {

    // action names
    public static final String NEW_LINE = "NOVI_REDAK";
    public static final String CHANGE_STATE = "UDJI_U_STANJE";
    public static final String GO_BACK = "VRATI_SE";
    public static final String SKIP = "-";

    private List<String> stateNames;
    private List<String> lexClasses;
    private HashMap<String, List<LexRule>> states;
    private LexAutomatonHandler handler;

    private String currLine;

    /**
     * Creates new instance of {@link LexicalInputParser} which reads given
     * input and parses it.
     * 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public LexicalInputParser(InputStream input) {
        stateNames = new ArrayList<>();
        lexClasses = new ArrayList<>();
        states = new LinkedHashMap<>();
        handler = LexAutomaton.getHandler();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses definitions for generator of lexical analyzer.
     * 
     * @throws IOException
     */
    private void parse(BufferedReader reader) throws IOException {
        readRegDef(reader);
        readStates(reader);
        readLexClasses(reader);
        readRules(reader);
    }

    /**
     * Reads regular definitions from input.
     * 
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
    private void readRegDef(BufferedReader reader) throws IOException {
        while (true) {
            currLine = reader.readLine();
            if (currLine.startsWith("%X")) {
                break;
            }

            // removed splitting by space because maybe we can have
            // {regular<space>definition} -> regex
            int regdefEnd = currLine.indexOf('}');
            String regDefName = currLine.substring(1, regdefEnd);
            String regEx = currLine.substring(regdefEnd + 2);
            //AutomatonCreator.addRegularDefinition(regDefName, regEx);
            handler.addRegularDefinition(regDefName, regEx);
        }
    }

    /**
     * Reads states from input.
     * 
     * @param reader for
     * @param line read line
     */
    private void readStates(BufferedReader reader) {
        currLine = currLine.substring(3);
        String[] statesArray = currLine.split("\\s");
        for (String state : statesArray) {
            stateNames.add(state);
        }
    }

    /**
     * Reads lexical classes from input.
     * 
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
    private void readLexClasses(BufferedReader reader) throws IOException {
        currLine = reader.readLine().substring(3);
        String[] lexClassesArray = currLine.split("\\s");
        for (String lexClass : lexClassesArray) {
            lexClasses.add(lexClass);
        }
    }

    /**
     * Reads rules from input.
     * 
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
    private void readRules(BufferedReader reader) throws IOException {
        while ((currLine = reader.readLine()) != null) {
            int idx = currLine.indexOf('>');
            String state = currLine.substring(1, idx);
            String regEx = currLine.substring(idx + 1);
            // Automaton<Character> automaton = AutomatonCreator.fromString(regEx);
            Automaton<Character> automaton = handler.fromString(regEx);

            reader.readLine(); // reads the { symbol
            String lexClass = reader.readLine();
            List<LexAction> actions = new LinkedList<>();
            while (!(currLine = reader.readLine()).startsWith("}")) {
                actions.add(createAction(currLine));
            }

            List<LexRule> lexRules = states.get(state);
            if (lexRules == null) {
                lexRules = new LinkedList<>();
            }
            lexRules.add(new LexRule(lexClass, automaton, actions));
            states.put(state, lexRules);
        }
    }

    /**
     * Recognizes action type from read line.
     * 
     * @param line which contains some action
     * @return action parsed from given line
     */
    private LexAction createAction(String line) {
        String args[] = line.split("\\s");
        /*
         * This iffing can be avoided by little modification of creating
         * abstract class which implements IAction interface and has private
         * variable String for argument, and a few manipulations with creating
         * new instances of IActions ...
         */
        /*
         * Also, this part is very insensitive about errors: if first argument
         * of action is not recognized as some action name, it is assumed that
         * it is lexical class. But, input file is said to be always properly
         * formatted so there shouldn't be any errors.
         */
        if (args[0].equals(NEW_LINE)) {
            return new NewLineAction();
        } else if (args[0].equals(SKIP)) {
            return new SkipAction();
        } else if (args[0].equals(GO_BACK)) {
            return new GoBackAction(Integer.parseInt(args[1]));
        } else if (args[0].equals(CHANGE_STATE)) {
            return new ChangeStateAction(args[1]);
        } else {
            throw new IllegalArgumentException("Undefined action: " + args[0]);
        }
    }

    /**
     * Returns start state of lexical analyzer if such one exists.
     * 
     * @return start state of lexical analyzer
     * @throws NoSuchElementException if there is no start state, ie. no states
     *             at all.
     */
    public String getStartState() {
        if (stateNames.isEmpty()) {
            throw new NoSuchElementException("There is no start state.");
        }
        return stateNames.get(0);
    }

    /**
     * Returns map of rules for lexical analyzer in order as they are found in
     * input file.
     * 
     * @return map of rules for lexical analyzer
     */
    public HashMap<String, List<LexRule>> getStates() {
        return states;
    }

    /**
     * Gives the {@link AutomatonHandler}.
     * 
     * @return automaton handler
     */
    public LexAutomatonHandler getLexAutomatonHandler() {
        return handler;
    }

    /**
     * Returns a list of lexical analyzer states.
     * 
     * @return list of states of lexical analyzer
     */
    public List<String> getStateNames() {
        return stateNames;
    }

    /**
     * Returns a list of lexical classes.
     * 
     * @return list of lexical classes
     */
    public List<String> getLexClasses() {
        return lexClasses;
    }

}
