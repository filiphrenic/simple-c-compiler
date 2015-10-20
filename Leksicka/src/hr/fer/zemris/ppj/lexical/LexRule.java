package hr.fer.zemris.ppj.lexical;

import java.io.Serializable;
import java.util.List;

import hr.fer.zemris.ppj.actions.IAction;
import hr.fer.zemris.ppj.automaton.Automaton;

/**
 * This class represent a rule that is executed when this rule's regex matches a
 * part of the input.
 * 
 * @author fhrenic
 */
public class LexRule implements Serializable {

    private static final long serialVersionUID = -3606885506411201521L;

    private String lexClass;
    private Automaton automaton;
    private List<IAction> actions;

    /**
     * Creates a new rule from a given automaton (regex), and a list of actions
     * that are executed when input matches regex.
     * 
     * @param automaton regex
     * @param actions list of actions that are executeds
     */
    public LexRule(String lexClass, Automaton automaton, List<IAction> actions) {
        this.lexClass = lexClass;
        this.automaton = automaton;
        this.actions = actions;
    }

    /**
     * Returns true if this lex rule adds a new lex class, false if it skips the
     * current input.
     * 
     * @return <code>true</code> if it adds a lex class
     */
    public boolean hasLexClass() {
        return !"-".equals(lexClass.trim());
    }

    /**
     * Returns this rule's lex class.
     * 
     * @return lex class
     */
    public String lexClass() {
        return lexClass;
    }

    /**
     * Returns automaton that is used to match a regex for this rule.
     * 
     * @return automaton that matches regex
     */
    public Automaton getAutomaton() {
        return automaton;
    }

    /**
     * Executes all actions for this rule.
     * 
     * @param lex analyzer
     */
    public void execute(Lex lex) {
        for (IAction action : actions) {
            action.execute(lex);
        }
    }

}
