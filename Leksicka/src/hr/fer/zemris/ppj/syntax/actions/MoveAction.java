package hr.fer.zemris.ppj.syntax.actions;

import hr.fer.zemris.ppj.syntax.LRParser;

/**
 * @author fhrenic
 */
public class MoveAction implements LRAction {

    private static final long serialVersionUID = 190351265170535630L;

    private Integer newState;

    public MoveAction(Integer newState) {
        this.newState = newState;
    }

    @Override
    public void execute(LRParser parser) {
        parser.executeMove(newState);
    }

}
