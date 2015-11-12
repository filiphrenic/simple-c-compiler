package hr.fer.zemris.ppj.syntax;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author fhrenic
 * @author marko1597
 */
public class LRNode {

    public static void main(String[] args) {
        //        Stack<Integer> x = new Stack<>();
        Queue<Integer> x = new LinkedList<>();
        x.add(2);
        x.add(3);
        Iterable<Integer> a = x;
        for (Integer b : a) {
            System.out.println(b);
        }
    }

    private LRSymbol symbol;
    private List<LRNode> children;

    public LRNode(LRSymbol symbol) {
        this.symbol = symbol;
        children = new LinkedList<>();
    }

    public LRSymbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int level) {
        StringBuilder sb = new StringBuilder();
        String indent = spaces(level);
        sb.append(indent);
        sb.append(symbol);
        for (LRNode child : children) {
            sb.append('\n');
            sb.append(child.toString(level + 1));
        }
        return sb.toString();
    }

    private static String spaces(int x) {
        return new String(new char[x]).replace('\0', ' ');
    }

    public void reverseChildren() {
        // needs to be done to get the correct order
        Collections.reverse(children);
    }

    public void addChild(LRNode child) {
        children.add(child);
    }
}
