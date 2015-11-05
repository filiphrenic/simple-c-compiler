package hr.fer.zemris.ppj.sintax;

import java.util.List;

/**
 * @author fhrenic
 * @author marko1597
 */
public class Production {

    private Symbol lhs; // left hand side
    private List<Symbol> rhs; // right hand side;

    public List<Symbol> GetLeftHandSide(){
    	return rhs;
    }
    public List<Symbol> GetRightHandSide(){
    	return rhs;
    }
}
