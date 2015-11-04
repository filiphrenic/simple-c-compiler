package hr.fer.zemris.ppj.sintax;

import java.util.List;

/**
 * @author fhrenic
 */
public class LRNode {

    private List<LRNode> children;

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int level){
    	String indent = String.format("%" + level + "s", "");
    	String ret = indent + "this node";
    	for (LRNode node : children){
    		ret += node.toString(level+1);
    	}
    	return ret;
    }

}
