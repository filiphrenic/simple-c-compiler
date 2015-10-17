package hr.fer.zemris.ppj.lex.actions;

import java.io.Serializable;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * 
 * @author fhrenic
 */
public interface IAction extends Serializable {

    public void execute(Lex lex);

}
