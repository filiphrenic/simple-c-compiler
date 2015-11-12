package hr.fer.zemris.ppj.sintax.actions;

import java.io.Serializable;

import hr.fer.zemris.ppj.sintax.LRParser;

/**
 * @author fhrenic
 */
public interface LRAction extends Serializable {

    public void execute(LRParser parser);

}
