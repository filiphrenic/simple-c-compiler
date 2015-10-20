package hr.fer.zemris.ppj.actions;

import hr.fer.zemris.ppj.lexical.Lex;

/**
 * Returns a part of the input to the lexical analyzer when called.
 * 
 * @author fhrenic
 */
public class GoBackAction implements IAction {

    private static final long serialVersionUID = 8238297743771818369L;

    private int goBack;

    /**
     * Creates a new action that will return symbols into lexical analyzers
     * input.
     * 
     * @param goBack number of symbols that aren't returned
     */
    public GoBackAction(int goBack) {
        this.goBack = goBack;
    }

    @Override
    public void execute(Lex lex) {
        lex.goBack(goBack);
    }

}
