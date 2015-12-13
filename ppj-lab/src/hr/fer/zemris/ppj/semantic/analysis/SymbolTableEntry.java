package hr.fer.zemris.ppj.semantic.analysis;

import hr.fer.zemris.ppj.semantic.types.NumericType;
import hr.fer.zemris.ppj.semantic.types.Type;

/**
 * @author fhrenic
 */
public class SymbolTableEntry {

    private Type type;

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public boolean isLExpression() {
        return type instanceof NumericType;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

}
