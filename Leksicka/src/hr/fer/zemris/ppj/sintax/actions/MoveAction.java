package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRAction;
import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public class MoveAction implements LRAction {

    private Integer newState;

    public MoveAction(Integer newState) {
        this.newState = newState;
    }

    @Override
    public void execute(LRParser parser) {
    	parser.MoveAction(this.newState);
    }

}
