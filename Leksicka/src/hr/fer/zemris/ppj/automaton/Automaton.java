package hr.fer.zemris.ppj.automaton;

import java.io.Serializable;

/**
 * This interface provides methods for handling finite state automatons. Sym
 * parameter is generic, it is the type of symbol that the automaton can
 * consume.
 * 
 * @author fhrenic
 */
public interface Automaton<Sym> extends Serializable {

    /**
     * Applies transition based on the given symbol.
     * 
     * @param symbol transition symbol
     */
    public void consume(Sym symbol);

    /**
     * Puts the automaton in the starting position.
     */
    public void reset();

    /**
     * Tests if the automaton is in an acceptable state.
     * 
     * @return <code>true</code> if automaton accepts
     */
    public boolean accepts();

    /**
     * Automaton is dead if there is no current state. Can end up dead if it
     * consumes a symbol from a state that doesn't provide a transition for that
     * symbol.
     * 
     * @return <code>true</code> if automaton is dead
     */
    public boolean isDead();

}
