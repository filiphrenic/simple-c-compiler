package hr.fer.zemris.ppj.actions;

import hr.fer.zemris.ppj.lexical.Lex;

/**
 * This action skips the current input in the analyzer.
 * 
 * @author fhrenic
 */
public class SkipAction implements IAction {

    private static final long serialVersionUID = -1595787215215956648L;

    @Override
    public void execute(Lex lex) {
        lex.skip();
    }

}
