package hr.fer.zemris.ppj.lex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import hr.fer.zemris.ppj.automaton.Automaton;
import hr.fer.zemris.ppj.automaton.AutomatonHandler;
import hr.fer.zemris.ppj.stream.Streamer;

/**
 * 
 * @author fhrenic
 */
public class Lex {

    private HashMap<String, List<LexRule>> states;
    private String currentState;
    private List<LexRule> currentRules;

    private int startIndex;
    private int endIndex;
    private int lastIndex;

    private int lineNumber;

    private InputStream input;
    private OutputStream output;
    private String in;

    public Lex(String startState, HashMap<String, List<LexRule>> states, AutomatonHandler handler,
            InputStream input, OutputStream output) {
        this.currentState = startState;
        this.states = states;
        this.input = input;
        this.output = output;
        Automaton.setHandler(handler); // don't change this
        startIndex = 0; // pocetak
        endIndex = 0; // zavrsetak
        lastIndex = -1; // posljednji
        lineNumber = 1;
    }

    public void analyzeInput() throws IOException {
        // we should save the table that we need to print out
        // maybe it will be needed in later exercises so we won't 
        // have to parse it

        in = Streamer.readFromStream(input);
        int len = in.length();
        currentRules = states.get(currentState);
        LexRule lastRule = null;

        while (endIndex < len) {
            char symbol = in.charAt(endIndex);
            boolean allDead = true;
            for (LexRule rule : currentRules) {
                Automaton cur = rule.getAutomaton();
                if (cur.isDead()) {
                    continue;
                }
                allDead = false;
                cur.consume(symbol);
                if (cur.accepts()) {
                    lastRule = rule;
                    lastIndex = endIndex;
                }
            }
            if (allDead) {
                if (lastRule == null) {
                    error();
                    endIndex = startIndex;
                    startIndex++;
                } else {
                    lastRule.execute(this);
                    startIndex = lastIndex + 1;
                    endIndex = lastIndex;
                }
                lastRule = null;
                for (LexRule rule : currentRules) {
                    rule.getAutomaton().reset();
                }
            }
            endIndex++;
        }
    }

    public void incrementLineNumber() {
        lineNumber++;
    }

    public void printLexClass(String lexClass) {
        String sub = in.substring(startIndex, lastIndex);
        System.out.println(lexClass + " " + lineNumber + " " + sub);
    }

    public void goBack(int toIdx) {
        int idx = startIndex + toIdx - 1;
        endIndex = idx;
        lastIndex = idx;
    }

    public void changeState(String state) {
        currentState = state;
    }

    public void skip() {
        startIndex = endIndex + 1;
    }

    private void error() {
        int leftBound = Math.max(0, startIndex - 4);
        int rightBound = Math.min(startIndex + 4, in.length());
        System.err.println("Error at " + in.substring(leftBound, rightBound));
    }

}
