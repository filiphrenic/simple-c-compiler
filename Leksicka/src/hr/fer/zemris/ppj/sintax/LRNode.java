package hr.fer.zemris.ppj.sintax;

import java.util.List;

import hr.fer.zemris.ppj.sintax.grammar.Symbol;

/**
 * @author fhrenic
 * @author marko1597
 */
public class LRNode {

    private List<LRNode> children;
    String LRNodeName;
    
    public LRNode( Symbol sym ) {
		this.LRNodeName = sym.toString();
	}
    public LRNode( LRSymbol sym ) {
		this.LRNodeName = sym.toString();
	}
    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int level){
    	String indent = String.format("%" + level + "s", "");
    	String ret = indent + LRNodeName;
    	for (LRNode node : children){
    		ret += node.toString(level+1);
    	}
    	return ret;
    }
    
    public void AddChild( LRNode child ){
    	this.children.add(child);
    }
}
