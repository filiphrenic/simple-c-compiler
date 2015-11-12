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
        StackEntry startingSE = new StackEntry(0, null);
        stack.push(startingSE);

        symbols = LRSymbol.readSymbolsFrom(input);
        index = 0;

        while (!accepts) {
            LRSymbol current = symbols.get(index);

            Map<Symbol, LRAction> map = actions.get(stack.peek().state);

            if (map != null) {
                LRAction action = map.get(current.getSymbol());
                if (action == null) {
                    errorRecovery();
                    return;
                }
                action.execute(this);
            } else {
                errorRecovery();

                return;
            }
        }

        // print tree
        try {
            Streamer.writeToStream(stack.peek().node, output);
        } catch (IOException e) {
        }
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
            StackEntry se = new StackEntry(newState, parent);
            stack.push(se);
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
        System.err.println("Searching for synchronization symbol...");

        LRSymbol sync;
        while (!(sync = symbols.get(index)).getSymbol().isSync()) {
            index++;
        }

        System.err.println("Found symbol " + sync);

        // searching for valid transition
        while (true) {
            Integer state = stack.peek().state;
            Map<Symbol, LRAction> map = actions.get(state);
            if (map == null || map.get(sync.getSymbol()) == null) {
                stack.pop();
                continue;
            }
            break;
        }
        System.err.println("Continuing with the analysis...");
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
