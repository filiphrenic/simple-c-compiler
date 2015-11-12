package hr.fer.zemris.ppj.syntax.actions;

import hr.fer.zemris.ppj.syntax.LRParser;
import hr.fer.zemris.ppj.syntax.grammar.Production;

/**
 * @author fhrenic
 */
public class ReduceAction implements LRAction {

    private static final long serialVersionUID = -3146584488477220887L;

    private Production production;

    public ReduceAction(Production production) {
        this.production = production;
    }

    @Override
    public void execute(LRParser parser) {
        parser.executeReduce(production);
    }

}
