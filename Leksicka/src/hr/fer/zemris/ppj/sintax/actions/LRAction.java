package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public interface LRAction {

    public void execute(LRParser parser);

}
