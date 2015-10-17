package hr.fer.zemris.ppj.lex;

import java.io.Serializable;
import java.util.List;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.lex.actions.IAction;

public class LexRule implements Serializable {

    private static final long serialVersionUID = -3606885506411201521L;
    private List<IAction> actions;
    private Automaton automaton;

    public LexRule(Automaton automaton, List<IAction> actions) {
        this.automaton = automaton;
        this.actions = actions;
    }

    public void execute(Lex lex) {
        for (IAction action : actions) {
            action.execute(lex);
        }
    }

    public Automaton getAutomaton() {
        return automaton;
    }

}
