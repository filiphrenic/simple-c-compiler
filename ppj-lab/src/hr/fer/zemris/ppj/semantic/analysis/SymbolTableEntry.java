package hr.fer.zemris.ppj.semantic.analysis;

import hr.fer.zemris.ppj.semantic.types.NumericType;
import hr.fer.zemris.ppj.semantic.types.Type;

/**
 * @author fhrenic
 */
public class SymbolTableEntry {

    private Type type;
    private boolean defined;

    public SymbolTableEntry(Type type) {
        this.type = type;
        defined = false;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLExpression() {
        return type instanceof NumericType;
    }

    public boolean getDefined() {
        return defined;
    }

    /**
     */
    public void setDefined() {
        defined = true;
    }

}
