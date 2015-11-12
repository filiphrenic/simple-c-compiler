package hr.fer.zemris.ppj.syntax.actions;

import java.io.Serializable;

import hr.fer.zemris.ppj.syntax.LRParser;

/**
 * @author fhrenic
 */
public interface LRAction extends Serializable {

    public void execute(LRParser parser);

}
