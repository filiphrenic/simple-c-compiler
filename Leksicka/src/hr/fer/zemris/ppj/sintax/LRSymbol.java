package hr.fer.zemris.ppj.sintax;

import hr.fer.zemris.ppj.sintax.grammar.Symbol;

public class LRSymbol {
	int LineNumber;
	String originalText; 
	Symbol sym;
	public LRSymbol(Symbol s, int lineOfOccurrence, String originalText) {
		this.sym = s;
		this.LineNumber = lineOfOccurrence;
		this.originalText = originalText;
	}
	public LRSymbol(Symbol s) {
		this.sym = s;
		this.LineNumber = -1;
		this.originalText = "";
	}
	public int getLineNumber() {
		return this.LineNumber;
	}
	public String getOriginalText(){
		return this.originalText;
	}
}
