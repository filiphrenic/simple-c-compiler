package hr.fer.zemris.ppj.sintax;

import hr.fer.zemris.ppj.sintax.grammar.Symbol;

public class LRSymbol extends Symbol {
	int LineNumber;
	String originalText; 
	public LRSymbol(Symbol s, int lineOfOccurrence, String originalText) {
		super(s.getType(), s.toString());
		this.LineNumber = lineOfOccurrence;
		this.originalText = originalText;
	}
	public LRSymbol(Symbol s) {
		super(s.getType(), s.toString());
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
