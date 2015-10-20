package hr.fer.zemris.ppj.actions;

import hr.fer.zemris.ppj.lexical.Lex;

/**
 * This action increments the number of lines in the analyzer.
 * 
 * @author fhrenic
 */
public class NewLineAction implements IAction {

    private static final long serialVersionUID = 5071566988664731887L;

    @Override
    public void execute(Lex lex) {
        lex.incrementLineNumber();
    }

}
