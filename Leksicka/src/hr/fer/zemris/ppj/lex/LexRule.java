package hr.fer.zemris.ppj.lex;

import java.util.List;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.lex.actions.IAction;

public class LexRule {

    private List<IAction> actions;
    private Automaton automaton;

    public void execute(Lex lex) {
        for (IAction action : actions) {
            action.execute(lex);
        }
    }

}
