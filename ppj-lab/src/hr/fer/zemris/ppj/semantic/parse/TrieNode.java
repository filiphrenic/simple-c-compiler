package hr.fer.zemris.ppj.semantic.parse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fhrenic
 */
public class TrieNode<Key, Value> {

    private Value value;
    private Map<Key, TrieNode<Key, Value>> transitions;

    public TrieNode() {
        value = null;
        transitions = new HashMap<>();
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public TrieNode<Key, Value> getTransition(Key key) {
        return transitions.get(key);
    }

    public void addTransition(Key key, TrieNode<Key, Value> next) {
        transitions.put(key, next);
    }

}
