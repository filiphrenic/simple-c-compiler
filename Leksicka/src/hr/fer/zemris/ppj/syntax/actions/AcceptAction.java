package hr.fer.zemris.ppj.syntax.actions;

import hr.fer.zemris.ppj.syntax.LRParser;

/**
 * @author fhrenic
 */
public class AcceptAction implements LRAction {

    private static final long serialVersionUID = 6912391556217668021L;

    @Override
    public void execute(LRParser parser) {
        parser.executeAccept();
    }
}
