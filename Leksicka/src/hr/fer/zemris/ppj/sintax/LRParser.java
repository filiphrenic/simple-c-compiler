package hr.fer.zemris.ppj.sintax;

import java.awt.Desktop.Action;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

/**
 * @author fhrenic
 * @author marko1597
 */
public class LRParser {
	Stack<Symbol> stackSymbol;
	Stack<Integer> stackState;
	Vector<Symbol> input;
	Symbol currentSym;
	int inputindex;
	int StartState;
	boolean running;
	Symbol StartStackSymbol;
    // stanje -> ( znak -> akcija )
    // Akcija i NovoStanje su objedinjeni u ovoj
    // zato jer nemaju presjeka, Akcija je definirana za stanje i završni,
    // a NovoStanje je definirana za stanje i nezavršni znak
    private Map<Integer, Map<Symbol, LRAction>> actions;
    LRNode tree;
    
    public LRParser( int startState, Symbol startStackSymbol, Map<Integer, Map<Symbol, LRAction>> table ) {
		this.StartStackSymbol = startStackSymbol;
		this.StartState = startState;
		this.actions = table;
		this.inputindex = 0;
	}
    
    public LRNode analyzeInput(Vector<Symbol> input) throws IOException {
    	this.input = input;
    	running = true;
    	stackState.push(StartState);
    	stackSymbol.push(StartStackSymbol);
    	while(running)
    		actions.get(stackState.peek()).get(currentSym).execute(this);
    	
    	return tree;
    }
    public void AcceptAction(){
    	System.out.println(tree.toString());
    	this.running = false;
    }
    public void MoveAction( Integer newState ){
    	currentSym = input.elementAt(this.inputindex++);
    	stackState.push(newState);
    	stackSymbol.push(currentSym);
    }
    public void PutAction( Integer newState ){
    	stackState.push(newState);
    }
    public void ReduceAction( Production production ){
    	//todo tree generation
    	for( int i = 0; i < production.GetRightHandSide().size(); i++ ){
    		stackState.pop();
    		stackSymbol.pop();
    	}
    	stackSymbol.push(production.GetLeftHandSide());
    	actions.get(stackState.peek()).get(stackSymbol.peek()).execute(this);
    }
}
