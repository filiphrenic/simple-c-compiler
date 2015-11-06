package hr.fer.zemris.ppj.sintax.actions;

import hr.fer.zemris.ppj.sintax.LRAction;
import hr.fer.zemris.ppj.sintax.LRParser;
import hr.fer.zemris.ppj.sintax.Production;

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
        parser.ReduceAction(this.production);
    }

}
