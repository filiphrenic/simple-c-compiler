package hr.fer.zemris.ppj.sintax;

import java.io.IOException;
import java.nio.channels.Pipe.SinkChannel;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import hr.fer.zemris.ppj.sintax.actions.LRAction;
import hr.fer.zemris.ppj.sintax.grammar.Production;
import hr.fer.zemris.ppj.sintax.grammar.Symbol;

/**
 * @author fhrenic
 * @author marko1597
 */
public class LRParser {
    Stack<LRSymbol> stackSymbol;
    Stack<Integer> stackState;
    Stack<LRNode> stackNode;
    Vector<LRSymbol> input;
    LRSymbol currentSym;
    int inputindex;
    int startState;
    boolean running;
    LRSymbol startStackSymbol;
    // stanje -> ( znak -> akcija )
    // Akcija i NovoStanje su objedinjeni u ovoj
    // zato jer nemaju presjeka, Akcija je definirana za stanje i završni,
    // a NovoStanje je definirana za stanje i nezavršni znak
    private Map<Integer, Map<Symbol, LRAction>> actions;
    LRNode tree;

    public LRParser(int startState, LRSymbol startStackSymbol,
            Map<Integer, Map<Symbol, LRAction>> table) {
        this.startStackSymbol = startStackSymbol;
        this.startState = startState;
        this.actions = table;
        this.inputindex = 0;
    }

    public LRNode analyzeInput(Vector<LRSymbol> input) throws IOException {
        this.input = input;
        LRAction action;
        running = true;
        stackState.push(startState);
        stackSymbol.push(startStackSymbol);
        while (running) {
            action = actions.get(stackState.peek()).get(currentSym);
            if (action != null)
                action.execute(this);
            else
                this.errorRecovery();
        }
        return tree;
    }

    public void acceptAction() {
        System.out.println(tree.toString());
        this.running = false;
        this.tree = stackNode.peek();
    }

    public void moveAction(Integer newState) {
        currentSym = input.elementAt(this.inputindex++);
        stackState.push(newState);
        stackSymbol.push(currentSym);
        stackNode.push(new LRNode(currentSym));
    }

    public void putAction(Integer newState) {
        stackState.push(newState);
        this.currentSym = input.elementAt(this.inputindex);
    }

    public void errorRecovery() {
        System.err.println("Error at line:" + currentSym.getLineNumber());

        //todo 2. ocekivani uniformni znakovi (oni znakovi koji ne bi izazvali pogresku)

        System.err.printf("readed %s it is in input text %s", currentSym.toString(),
                currentSym.getOriginalText());
        
        //todo provjera jeli sinkronizacijski znak
        while (input.elementAt(this.inputindex).isSync() == false) {
            this.inputindex++;
        }
        currentSym = input.elementAt(this.inputindex);
        while (actions.get(stackState.peek()).get(currentSym) == null) {
            stackState.pop();
            stackNode.pop();
            stackSymbol.pop();
        }
    }

    public void reduceAction(Production production) {
        LRNode tmp = new LRNode(production.getLHS());
        Stack<LRNode> tmpStack = new Stack<LRNode>();
        for (int i = 0; i < production.getRHS().size(); i++) {
            stackState.pop();
            stackSymbol.pop();
            tmpStack.push(stackNode.peek());
            stackNode.pop();
        }
        while(!tmpStack.isEmpty()){
        	tmp.AddChild(tmpStack.peek());
        	tmpStack.pop();
        }
        stackSymbol.push(new LRSymbol(production.getLHS()));
        stackNode.push(tmp);
        //put action
        this.currentSym = stackSymbol.peek();
    }
}
