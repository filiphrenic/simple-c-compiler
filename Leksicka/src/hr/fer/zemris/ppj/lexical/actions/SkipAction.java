package hr.fer.zemris.ppj.lexical.actions;

import hr.fer.zemris.ppj.lexical.Lex;
import hr.fer.zemris.ppj.lexical.LexAction;

/**
 * This action skips the current input in the analyzer.
 * 
 * @author fhrenic
 */
public class SkipAction implements LexAction {

    private static final long serialVersionUID = -1595787215215956648L;

    @Override
    public void execute(Lex lex) {
        lex.skip();
    }

}
