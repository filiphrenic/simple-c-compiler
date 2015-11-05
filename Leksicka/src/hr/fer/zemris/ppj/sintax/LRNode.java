package hr.fer.zemris.ppj.sintax;

import java.util.List;

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
    public void addLeaf( Production production ){
    	for (Symbol elem:production.GetRightHandSide()) {
			children.add(new LRNode(elem));
		}
    }
}
