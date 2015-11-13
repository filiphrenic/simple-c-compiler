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
 * @author fhrenic
 */
public class LRParser {

    private InputStream input;
    private OutputStream output;
    private Map<Integer, Map<Symbol, LRAction>> actions;
    private Map<Integer, Map<Symbol, Integer>> newStates;

    private Stack<StackEntry> stack;
    private boolean accepts;

    private List<LRSymbol> symbols;
    private int index;

    public LRParser(InputStream input, OutputStream output,
            Map<Integer, Map<Symbol, LRAction>> actions,
            Map<Integer, Map<Symbol, Integer>> newStates) {
        this.input = input;
        this.output = output;
        this.actions = actions;
        this.newStates = newStates;
        accepts = false;
    }

    public void parse() {
        stack = new Stack<>();
        stack.push(new StackEntry(0, null));

        symbols = LRSymbol.readSymbolsFrom(input);
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

        System.err.println("Accepts: " + accepts);
    }

    public void executeMove(int newState) {
        LRSymbol current = symbols.get(index++);
        StackEntry se = new StackEntry(newState, new LRNode(current));
        stack.push(se);
    }

    public void executeReduce(Production p) {
        LRNode parent = new LRNode(new LRSymbol(p.getLHS()));

        if (!p.isEpsilonProduction()) {
            for (int idx = p.getRHS().size() - 1; idx >= 0; idx--) {
                Symbol sym = p.getRHS().get(idx);
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
        parent.reverseChildren();

        Map<Symbol, Integer> map = newStates.get(stack.peek().state);
        if (map != null) {
            Integer newState = map.get(parent.getSymbol().getSymbol());
            if (newState == null) {
                errorRecovery();
                return;
            }
            stack.push(new StackEntry(newState, parent));
        } else {
            errorRecovery();
            return;
        }

    }

    public void executeAccept() {
        accepts = true;
    }

    private void errorRecovery() {

        System.err.println("Error at " + symbols.get(index).getLineNumber());
        System.err.println("Expected one of following: ");
        for (Symbol s : actions.get(stack.peek().state).keySet()) {
            System.err.print(s + " ");
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
            Map<Symbol, LRAction> map = actions.get(stack.peek().state);
            if (map == null || map.get(sync.getSymbol()) == null) {
                stack.pop();
                if (stack.isEmpty()) {
                    System.err.println(
                            "Error recovery hasn't come to a valid state. Stoping analysis");
                }
                continue;
            }
            break;
        }
        System.err.println("Continuing with the analysis...\n");
    }

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
