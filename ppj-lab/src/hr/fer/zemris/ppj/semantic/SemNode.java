package hr.fer.zemris.ppj.semantic;

import java.util.HashMap;
import java.util.Map;

import hr.fer.zemris.ppj.semantic.analysis.SemanticException;
import hr.fer.zemris.ppj.semantic.types.Type;
import hr.fer.zemris.ppj.util.Util;

/**
 * @author fhrenic
 */
public class SemNode {

    private String name;
    private Type type;
    private Map<Attribute, Object> attributes;

    public SemNode(String name) {
        this.name = name;
        attributes = new HashMap<>();
    }

    public void setAttribute(Attribute key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Cant set attribute " + key + " to null");
        }
        attributes.put(key, value);
    }

    public Object getAttribute(Attribute key) {
        Object value = attributes.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Attribute " + key + " not defined");
        }
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public Type getType() {
        if (type == null) {
            throw new SemanticException("Type not set", this);
        }
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    // helper, don't need these

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        return Util.spaces(indent) + repr(indent);
    }

    public String repr(int indent) {
        return "";
    }

}
