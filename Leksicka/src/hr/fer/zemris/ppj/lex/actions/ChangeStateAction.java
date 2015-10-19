package hr.fer.zemris.ppj.lex.actions;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * Changes current state of lexical analyzer.
 * 
 * @author fhrenic
 */
public class ChangeStateAction implements IAction {

    private static final long serialVersionUID = -3833574174991832894L;

    private String state;

    /**
     * Creates a new action that will change Lex's state when called.
     * 
     * @param state new state
     */
    public ChangeStateAction(String state) {
        this.state = state;
    }

    @Override
    public void execute(Lex lex) {
        lex.changeState(state);
    }

}
