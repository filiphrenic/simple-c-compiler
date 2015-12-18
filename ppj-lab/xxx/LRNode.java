package hr.fer.zemris.ppj.syntax;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import hr.fer.zemris.ppj.util.Util;

/**
 * Class that represents nodes of the parse tree created by the LR parser.
 * 
 * @author fhrenic
 * @author marko1597
 */
public class LRNode {

    private LRSymbol symbol;
    private List<LRNode> children;

    /**
     * Create a node with no children and given symbol.
     * 
     * @param symbol symbol
     */
    public LRNode(LRSymbol symbol) {
        this.symbol = symbol;
        children = new LinkedList<>();
    }

    /**
     * Reverses children order, needs to be done to get the correct order
     */
    public void reverseChildrenOrder() {
        Collections.reverse(children);
    }

    /**
     * Adds a child to this node.
     * 
     * @param child
     */
    public void addChild(LRNode child) {
        children.add(child);
    }

    /**
     * @return symbol
     */
    public LRSymbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(toString(0)).append('\n').toString();
    }

    /**
     * Creates a tree-like string representation of this node. This node is in
     * the first row, and each of it's children is indented in the next row. And
     * so on, and so on.
     * 
     * @param level indentation, tree depth
     * @return string representation
     */
    public String toString(int level) {
        StringBuilder sb = new StringBuilder();
        String indent = Util.spaces(level);
        sb.append(indent);
        sb.append(symbol);
        for (LRNode child : children) {
            sb.append('\n');
            sb.append(child.toString(level + 1));
        }
        return sb.toString();
    }

}
