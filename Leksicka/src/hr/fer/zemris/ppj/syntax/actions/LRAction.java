package hr.fer.zemris.ppj.syntax.actions;

import java.io.Serializable;

import hr.fer.zemris.ppj.syntax.LRParser;

/**
 * Interface for an action that is executed in the lr parser.
 * 
 * @author fhrenic
 */
public interface LRAction extends Serializable {

    public void execute(LRParser parser);

}
