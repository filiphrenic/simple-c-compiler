package hr.fer.zemris.ppj.lex.actions;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * 
 * @author fhrenic
 */
public class GoBackAction implements IAction {

    private static final long serialVersionUID = 8238297743771818369L;

    private int goBack;

    public GoBackAction(int goBack) {
        this.goBack = goBack;
    }

    @Override
    public void execute(Lex lex) {
        lex.goBack(goBack);
    }

}
