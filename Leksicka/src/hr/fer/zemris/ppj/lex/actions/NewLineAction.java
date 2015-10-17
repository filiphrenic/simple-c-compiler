package hr.fer.zemris.ppj.lex.actions;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * 
 * @author fhrenic
 */
public class NewLineAction implements IAction {

    private static final long serialVersionUID = 5071566988664731887L;

    /**
     * 
     */
    public NewLineAction() {
        System.out.println("aaaaaaa");
    }
    
    
    @Override
    public void execute(Lex lex) {
        lex.incrementLineNumber();
    }

}
