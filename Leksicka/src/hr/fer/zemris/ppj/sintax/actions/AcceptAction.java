package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public class AcceptAction implements LRAction {

    @Override
    public void execute(LRParser parser) {
        parser.acceptAction();
    }
}
