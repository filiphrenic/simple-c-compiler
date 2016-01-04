package hr.fer.zemris.ppj.semantic.analysis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import hr.fer.zemris.ppj.semantic.types.FunctionType;
import hr.fer.zemris.ppj.semantic.types.ListType;
import hr.fer.zemris.ppj.semantic.types.Type;

/**
 * @author fhrenic
 */
public class SymbolTable {

    private Type returnType;
    private Map<String, SymbolTableEntry> entries;
    private SymbolTable parent;
    private List<SymbolTable> nestedTables;

    private int numberOfParameters;
    private int ticketForLocals;

    public SymbolTable() {
        this(null);
    }

    public boolean isGlobal() {
        return parent == null;
    }

    private SymbolTable(SymbolTable parent) {
        this.parent = parent;
        if (parent != null) {
            returnType = parent.returnType;
            parent.nestedTables.add(this);
        }
        entries = new HashMap<>();
        nestedTables = new LinkedList<>();

        numberOfParameters = 0;
        ticketForLocals = 0;
    }

    /**
     * @return the returnType
     */
    public Type getReturnType() {
        return returnType;
    }

    /**
     * @param returnType
     */
    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public SymbolTableEntry getLocalEntry(String symbolName) {
        return entries.get(symbolName);
    }

    public SymbolTableEntry getEntry(String symbolName) {
        SymbolTableEntry ste = entries.get(symbolName);
        if (ste == null && parent != null) {
            return parent.getEntry(symbolName);
        }
        return ste;
    }

    public Set<Entry<String, SymbolTableEntry>> getAllEntries() {
        return entries.entrySet();
    }

    public void addParameters(List<String> names, ListType types) {
        numberOfParameters = names.size();
        for (int idx = 0; idx < numberOfParameters; idx++) {
            SymbolTableEntry ste = new SymbolTableEntry(types.getType(idx));
            ste.prepareOffset(true, idx);
            entries.put(names.get(idx), ste);
        }
    }

    public void addEntry(String symbolName, SymbolTableEntry ste) {
        entries.put(symbolName, ste);
        if (ste.getType() instanceof FunctionType) return;
        if (isGlobal()) ste.setGlobal();
        else ste.prepareOffset(false, ticketForLocals++);
    }

    public SymbolTable createNested() {
        return new SymbolTable(this);
    }

    public List<SymbolTable> getNestedTables() {
        return nestedTables;
    }

    public int getLocalSize() {
        return ticketForLocals;
    }

    public int getNumberOfParameters() {
        return numberOfParameters;
    }

}
