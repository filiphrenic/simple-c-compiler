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

    public void setAttributeRecursive(Attribute key, Object value) {
        setAttribute(key, value);
        for (SemNode child : children) {
            if (child instanceof SemNodeV) {
                ((SemNodeV) child).setAttributeRecursive(key, value);
            } else {
                child.setAttribute(key, value);
            }
        }
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
