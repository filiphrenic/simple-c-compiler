package hr.fer.zemris.ppj.semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhrenic
 */
public class SemNodeV extends SemNode {

    private boolean lexpression;
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

    public void setLExpr(boolean lexpr) {
        lexpression = lexpr;
    }

    public boolean getLExpr() {
        return lexpression;
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
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" ::=");
        for (SemNode n : children) {
            sb.append(' ');
            sb.append(n.represent());
        }
        return sb.toString();
    }

}
