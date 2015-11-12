package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public class AcceptAction implements LRAction {

    private static final long serialVersionUID = 6912391556217668021L;

    @Override
    public void execute(LRParser parser) {
        parser.acceptAction();
    }
}
