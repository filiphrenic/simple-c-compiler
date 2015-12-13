package hr.fer.zemris.ppj.semantic.analysis;

import java.util.HashMap;
import java.util.Map;

import hr.fer.zemris.ppj.semantic.types.Type;

/**
 * @author fhrenic
 */
public class SymbolTable {

    private Type returnType;
    private Map<String, SymbolTableEntry> entries;
    private SymbolTable parent;

    public SymbolTable() {
        this(null);
    }

    private SymbolTable(SymbolTable parent) {
        this.parent = parent;
        if (parent != null) {
            returnType = parent.returnType;
        }
        entries = new HashMap<>();
    }

    /**
     * @return the returnType
     */
    public Type getReturnType() {
        return returnType;
    }

    public SymbolTableEntry getEntry(String symbolName) {
        SymbolTableEntry ste = entries.get(symbolName);
        if (ste == null && parent != null) {
            return parent.getEntry(symbolName);
        }
        return ste;
    }

    public SymbolTable createNested() {
        return new SymbolTable(this);
    }

}
