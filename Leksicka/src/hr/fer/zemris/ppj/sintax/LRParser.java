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
	int StartState; 
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
	}
    
    public void analyzeInput(Vector<Symbol> input) throws IOException {
    	stackState.push(StartState);
    	stackSymbol.push(StartStackSymbol);
    	for( int i = 0; i < input.size(); i++ ){
    		actions.get(stackState.peek()).get(input).execute(this);
    	}
    }
    public void AcceptAction(){
    	
    }
    public void MoveAction( Integer newState ){
    	
    }
    public void PutAction( Integer newState ){
    	
    }
    public void ReduceAction( Production production ){
    	
    }
}
