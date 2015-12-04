package hr.fer.zemris.ppj.syntax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import hr.fer.zemris.ppj.stream.Streamer;
import hr.fer.zemris.ppj.syntax.actions.LRAction;
import hr.fer.zemris.ppj.syntax.grammar.Production;
import hr.fer.zemris.ppj.syntax.grammar.Symbol;

/**
 * Parser who's main task is to parse the input and build a syntax tree.
 * 
 * @author fhrenic
 */
public class LRParser {

    private OutputStream output;
    private Map<Integer, Map<Symbol, LRAction>> actions;
    private Map<Integer, Map<Symbol, Integer>> newStates;

    private Stack<StackEntry> stack;
    private boolean accepts;

    private List<LRSymbol> symbols;
    private int index;

    /**
     * Creates a new {@link LRParser}
     * 
     * @param input used to read symbols
     * @param output used to print the tree
     * @param actions actions taken by the parser
     * @param newStates determines the new state upon reduction
     */
    public LRParser(InputStream input, OutputStream output, List<String> syncSymbols,
            Map<Integer, Map<Symbol, LRAction>> actions,
            Map<Integer, Map<Symbol, Integer>> newStates) {
        this.output = output;
        this.actions = actions;
        this.newStates = newStates;
        symbols = LRSymbol.readSymbolsFrom(input, syncSymbols);
        accepts = false;
    }

    /**
     * Parses input from input stream.
     */
    public void parse() {
        stack = new Stack<>();
        stack.push(new StackEntry(0, null));
        index = 0;

        while (!accepts && index < symbols.size() && !stack.isEmpty()) {
            LRSymbol current = symbols.get(index);
            Map<Symbol, LRAction> map = actions.get(stack.peek().state);

            LRAction action;
            if (map == null || (action = map.get(current.getSymbol())) == null) {
                errorRecovery();
            } else {
                action.execute(this);
            }
        }

        // print tree
        try {
            Streamer.writeToStream(stack.peek().node, output);
        } catch (IOException e) {
        }
    }

    /**
     * Moves the pointer to the next symbol and builds a new node.
     * 
     * @param newState new state to go to
     */
    public void executeMove(int newState) {
        LRSymbol current = symbols.get(index++);
        stack.push(new StackEntry(newState, new LRNode(current)));
    }

    /**
     * Reduces nodes from the stack to a parent node and adds the removed nodes
     * as it's children.
     * 
     * @param p production, lhs becomes parent, rhs is removed from stack
     */
    public void executeReduce(Production p) {
        LRNode parent = new LRNode(new LRSymbol(p.getLHS()));

        if (!p.isEpsilonProduction()) {
            for (int idx = p.getSize() - 1; idx >= 0; idx--) {
                Symbol sym = p.getAt(idx);
                StackEntry se = stack.pop();
                if (!se.node.getSymbol().getSymbol().equals(sym)) {
                    errorRecovery();
                    return;
                }
                parent.addChild(se.node);
            }
        } else {
            parent.addChild(new LRNode(new LRSymbol(Symbol.EPS_SYMBOL)));
        }

        // needs to be done to have output ordering correct
        parent.reverseChildrenOrder();

        Map<Symbol, Integer> map = newStates.get(stack.peek().state);
        Integer newState;
        if (map != null && (newState = map.get(parent.getSymbol().getSymbol())) != null) {
            stack.push(new StackEntry(newState, parent));
        } else {
            errorRecovery();
        }

    }

    /**
     * Set parser as accepting the input.
     */
    public void executeAccept() {
        accepts = true;
    }

    /**
     * If there is no valid transition for the parser, this method is called.
     */
    private void errorRecovery() {

        System.err.println("Error at " + symbols.get(index).getLineNumber());
        System.err.println("Expected one of following: ");
        for (Symbol s : actions.get(stack.peek().state).keySet()) {
            System.err.println("\t" + s);
        }

        System.err.println("Searching for synchronization symbol...");

        LRSymbol sync;
        while (!(sync = symbols.get(index)).getSymbol().isSync()) {
            if (++index == symbols.size()) {
                System.err.println("Didn't find synchronization symbol");
                return;
            }
        }

        System.err.println("Found symbol " + sync);

        // searching for valid transition
        while (true) {
            if (stack.isEmpty()) {
                System.err.println("Error recovery hasn't come to a valid state. Stoping analysis");
                return;
            }
            Map<Symbol, LRAction> map = actions.get(stack.peek().state);
            if (map == null || map.get(sync.getSymbol()) == null) {
                stack.pop();
            } else {
                break;
            }
        }
        System.err.println("Continuing with the analysis...\n");
    }

    /**
     * Simple wrapper for a stack entry.
     * 
     * @author fhrenic
     */
    private static class StackEntry {
        int state;
        LRNode node;

        public StackEntry(int state, LRNode node) {
            this.state = state;
            this.node = node;
        }

        @Override
        public String toString() {
            return "[" + state + "," + node + "]";
        }
    }

}
