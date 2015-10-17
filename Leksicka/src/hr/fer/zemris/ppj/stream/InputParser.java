package hr.fer.zemris.ppj.stream;

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
import hr.fer.zemris.ppj.automaton.AutomatonHandler;
import hr.fer.zemris.ppj.lex.LexRule;
import hr.fer.zemris.ppj.lex.actions.AddLexClassAction;
import hr.fer.zemris.ppj.lex.actions.ChangeStateAction;
import hr.fer.zemris.ppj.lex.actions.GoBackAction;
import hr.fer.zemris.ppj.lex.actions.IAction;
import hr.fer.zemris.ppj.lex.actions.NewLineAction;
import hr.fer.zemris.ppj.lex.actions.SkipAction;

/**
 * Class which reads definitions for generator of lexical analyzer and offers
 * getters for states, lexical classes and rules for lexical analyzer.
 * 
 * @author fhrenic
 * @author ajuric
 */
public class InputParser {

    // action names
    public static final String NEW_LINE = "NOVI_REDAK";
    public static final String CHANGE_STATE = "UDJI_U_STANJE";
    public static final String GO_BACK = "VRATI_SE";
    public static final String SKIP = "-";

    private InputStream input;
    private List<String> stateNames;
    private List<String> lexClasses;
    private HashMap<String, List<LexRule>> states;
    private AutomatonHandler handler;

    /**
     * Creates new instance of {@link InputParser} which reads given input and
     * parses it.
     * 
     * @param input which contains definitions for generator of lexical analyzer
     */
    public InputParser(InputStream input) {
        this.input = input;
        handler = new AutomatonHandler();
        stateNames = new ArrayList<>();
        lexClasses = new ArrayList<String>();
        states = new LinkedHashMap<>();
        try {
            parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses definitions for generator of lexical analyzer.
     * 
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
     * 
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

            // removed splitting by space because maybe we can have
            // {regular definition} -> regex
            int regdefEnd = line.indexOf('}');
            String regDefName = line.substring(1, regdefEnd);
            String regEx = line.substring(regdefEnd + 2);
            handler.fromString(regEx, regDefName);
        }
    }

    /**
     * Reads states from input.
     * 
     * @param reader for
     * @param line read line
     */
    private void readStates(BufferedReader reader, String line) {
        line = line.substring(3);
        String[] statesArray = line.split("\\s");
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
     * 
     * @param reader for input
     * @param line read line
     * @throws IOException if error occurs while reading
     */
    private void readRules(BufferedReader reader, String line) throws IOException {
        while (line != null) {
            line = reader.readLine();
            String state = line.substring(1, line.indexOf(">"));
            String regEx = line.substring(line.indexOf(">") + 1);
            line = reader.readLine();
            Automaton automaton = handler.fromString(regEx, null);
            List<IAction> actions = new LinkedList<>();
            while (!line.equals("}")) {
                line = reader.readLine();
                actions.add(createAction(line));
            }
            List<LexRule> lexRules = states.get(state);
            if (lexRules == null) {
                lexRules = new LinkedList<>();
            }
            lexRules.add(new LexRule(automaton, actions));
            states.put(state, lexRules);
        }
    }

    /**
     * Recognizes action type from read line.
     * 
     * @param line which contains some action
     * @return action parsed from given line
     */
    private IAction createAction(String line) {
        String args[] = line.split("\\s");
        // uzasna ifovnjaca ...
        /*
         * This iffing can be avoided by little modification of creating abstract class which implements
         * IAction interface and has private variable String for argument, and a few manipulations with
         * creating new instances of IActions ...  
         */
        /*
         * Also, this part is very insensitive about errors: if first argument of action is not recognized
         * as some action name, it is assumed that it is lexical class. But, input file is said to be
         * always properly formatted so there shouldn't be any errors.
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
            return new AddLexClassAction(args[0]);
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
    public AutomatonHandler getAutomatonHandler() {
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
