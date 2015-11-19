package hr.fer.zemris.ppj.lexical.actions;

import java.io.Serializable;

import hr.fer.zemris.ppj.lexical.Lex;

/**
 * An interface for actions that are executed when a certain rule has to be
 * executed.
 * 
 * @author fhrenic
 */
public interface LexAction extends Serializable {

    /**
     * This method is called when a rule that has this action matches the input.
     * 
     * @param lex lexical analyzer that it will modify
     */
    public void execute(Lex lex);

}
