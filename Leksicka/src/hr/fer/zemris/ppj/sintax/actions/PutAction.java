package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public class PutAction implements LRAction {

    private Integer newState;

    public PutAction(Integer newState) {
        this.newState = newState;
    }

    @Override
    public void execute(LRParser parser) {
        parser.PutAction(this.newState);
    }
}
