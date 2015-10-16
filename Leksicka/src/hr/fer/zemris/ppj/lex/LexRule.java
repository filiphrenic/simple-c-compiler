package hr.fer.zemris.ppj.lex;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.lex.actions.IAction;

import java.util.List;

public class LexRule {

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

}
