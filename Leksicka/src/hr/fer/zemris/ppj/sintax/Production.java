package hr.fer.zemris.ppj.sintax;

import java.util.List;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production {

    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;

    public Symbol GetLeftHandSide(){
    	return lhs;
    }
    public List<Symbol> GetRightHandSide(){
    	return rhs;
    }
}
