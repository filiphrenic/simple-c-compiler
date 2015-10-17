package hr.fer.zemris.ppj.automaton;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents an epsilon non deterministic finite automaton.
 * 
 * @author fhrenic
 */
public class Automaton implements Serializable {

    // if speed optimization needed:
    //  - consider optimizing what times is the updateStates() called

    private static final long serialVersionUID = -173462860031922118L;

    public static void main(String[] args) {
        String regex;

        regex = "aaaa";
        System.out.println('\n' + regex);
        test(regex, regex);

        regex = "ab|ba";
        System.out.println('\n' + regex);
        test(regex, "ab");
        test(regex, "ba");
        test(regex, "aB");
        test(regex, "aba");

        regex = "ab*";
        System.out.println('\n' + regex);
        test(regex, "a");
        test(regex, "b");
        test(regex, "ab");
        test(regex, "abbbbbbbbbbb");

        handler.fromString("0|1", "bit");
        handler.fromString("\\n|\\t|\\_", "space");
        handler.fromString("{space}*", "spaces");
        handler.fromString("+|-", "op");
        regex = "{bit}{spaces}{op}{spaces}{bit}";
        System.out.println('\n' + regex);
        test(regex, "1          \n    +1");

    }

    private static void test(String regex, String text) {
        System.out.print("Testing " + text + " -> ");
        Automaton a = handler.fromString(regex, null);
        for (Character c : text.toCharArray()) {
            a.consume(c);
        }
        System.out.println(a.accepts);
    }

    /**
     * This object is used for providing and storing transitions. Automatons can
     * use transitions via this object.
     */
    protected static AutomatonHandler handler = new AutomatonHandler();

    /**
     * Sets a new handler. This method should be called when you have an
     * existing handler (generated and read from a file).
     * 
     * @param handler new handler
     */
    public static void setHandler(AutomatonHandler handler) {
        Automaton.handler = handler;
    }

    /**
     * Get the used handler. This method should be called right after all
     * automatons have been created.
     * 
     * @return automaton handler
     */
    public static AutomatonHandler getHandler() {
        return handler;
    }

    private int leftState;
    private int rightState; // this state is the only final state
    private Set<Integer> currentStates;
    private boolean accepts;

    /**
     * Creates an empty {@link Automaton} that only has 2 states but no
     * transitions.
     */
    public Automaton() {
        this(handler.getNewState(), handler.getNewState());
    }

    /**
     * Creates a new automaton with given left and right state. This should be
     * called only after you have added the transitions to the handler. If it's
     * done the other way around, it may not work properly.
     * 
     * @param leftState starting state
     * @param rightState final state
     */
    protected Automaton(int leftState, int rightState) {
        this.leftState = leftState;
        this.rightState = rightState;
        currentStates = new TreeSet<Integer>();
        currentStates.add(leftState);
        updateCurrentStates();
    }

    /**
     * Returns <code>true</code> if automaton is in acceptable state.
     * 
     * @return <code>true</code> if automaton accepts a string
     */
    public boolean accepts() {
        return accepts;
    }

    /**
     * Automaton is dead if the current state set is empty
     * 
     * @return <code>true</code> if automaton is dead
     */
    public boolean isDead() {
        return currentStates.isEmpty();
    }

    /**
     * Puts the automaton in the starting position.
     */
    public void reset() {
        currentStates = new TreeSet<>();
        currentStates.add(leftState);
        updateCurrentStates();
    }

    /**
     * Applies transitions based on the given symbol.
     * 
     * @param symbol transition symbol
     */
    public void consume(char symbol) {
        Set<Integer> states = new TreeSet<>();
        for (Integer state : currentStates) {
            Integer transitionState = handler.getNormalStates(state).get(symbol);
            if (transitionState != null) {
                states.add(transitionState);
            }
        }
        currentStates = states;
        updateCurrentStates();
    }

    /**
     * Adds a set of states to the current states
     * 
     * @param states states to add
     */
    protected void addStates(Set<Integer> states) {
        currentStates.addAll(states);
        updateCurrentStates();
    }

    /**
     * @return the leftState
     */
    protected int leftState() {
        return leftState;
    }

    /**
     * @return the rightState
     */
    protected int rightState() {
        return rightState;
    }

    /**
     * @return the currentStates
     */
    protected Set<Integer> getCurrentStates() {
        return currentStates;
    }

    /**
     * Updates the current states to the epilon environment of those states.
     */
    protected void updateCurrentStates() {
        // epsilon environment
        accepts = false;
        Set<Integer> states;
        do {
            states = new TreeSet<>();
            for (Integer state : currentStates) {
                if (state == rightState) {
                    accepts = true;
                }
                states.addAll(handler.getEpsilonStates(state));
            }
        } while (currentStates.addAll(states));

        if (!accepts && currentStates.contains(rightState)) {
            accepts = true;
        }
    }

}
