package hr.fer.zemris.ppj.lex.actions;

import hr.fer.zemris.ppj.lex.Lex;

/**
 * 
 * @author fhrenic
 */
public class ChangeStateAction implements IAction {

	private String state;

	public ChangeStateAction(String state) {
		this.state = state;
	}

	@Override
	public void execute(Lex lex) {
		// TODO Auto-generated method stub

	}

}
