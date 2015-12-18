package hr.fer.zemris.ppj.automaton;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fhrenic
 */
public class Mapper<T> {

    private int index;
    private Map<T, Integer> map;
    private Map<Integer, T> reverseMap;

    public Mapper(Collection<T> collection) {
        reverseMap = new HashMap<>();
        map = new HashMap<>();
        index = 0;
        for (T value : collection) {
            addValue(value);
        }
    }

    public void addValue(T value) {
        map.put(value, index);
        reverseMap.put(index, value);
        ++index;
    }

    public T getForIndex(int idx) {
        return reverseMap.get(idx);
    }

    public int getForValue(T value) {
        return map.get(value);
    }

}
