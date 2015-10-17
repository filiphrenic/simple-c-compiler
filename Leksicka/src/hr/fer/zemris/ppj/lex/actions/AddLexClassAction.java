package hr.fer.zemris.ppj.lex.actions;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * 
 * @author fhrenic
 */
public class AddLexClassAction implements IAction {

    private static final long serialVersionUID = -1742670871042699238L;
    
    private String className;

    public AddLexClassAction(String className) {
        this.className = className;
    }

    @Override
    public void execute(Lex lex) {
        // TODO Auto-generated method stub

    }

}
