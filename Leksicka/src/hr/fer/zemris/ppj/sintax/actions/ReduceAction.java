package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRParser;
import hr.fer.zemris.ppj.sintax.grammar.Production;

/**
 * @author fhrenic
 */
public class ReduceAction implements LRAction {

    private Production production;

    public ReduceAction(Production production) {
        this.production = production;
    }

    @Override
    public void execute(LRParser parser) {
        parser.reduceAction(this.production);
    }

}
