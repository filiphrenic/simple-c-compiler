package hr.fer.zemris.ppj.semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhrenic
 */
public class SemNodeV extends SemNode {

    private List<SemNode> children;

    /**
     * @param name
     */
    public SemNodeV(String name) {
        super(name);
        children = new ArrayList<>();
    }

    public int numOfChildren() {
        return children.size();
    }

    public SemNode getChild(int idx) {
        return children.get(idx);
    }

    public void addChild(SemNode child) {
        children.add(child);
    }

    @Override
    public String repr(int indent) {
        StringBuilder sb = new StringBuilder(getName());
        sb.append('\n');
        for (SemNode n : children) {
            sb.append(n.toString(indent + 1));
        }
        return sb.toString();
    }

}
