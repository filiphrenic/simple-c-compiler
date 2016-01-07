package hr.fer.zemris.ppj.semantic.analysis;

import hr.fer.zemris.ppj.semantic.types.NumberType;
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
        return type instanceof NumberType;
    }

    public boolean getDefined() {
        return defined;
    }

    /**
     */
    public void setDefined() {
        defined = true;
    }

    // for code generator

    private boolean global;
    private int offset;

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {

        /*
         * Lets say that main called function fun. Also, function fun takes 
         * 2 parameters and creates 2 local variables. This would be the stack:
         * 
         *  local_var_2_fun
         *  local_var_1_fun
         *  return_addr_to_main <=> frame_pointer_fun = R5
         *  frame_pointer_main
         *  parameter_2
         *  parameter_1
         *  ... (main local)
         * 
         * So we are adding this offset to the frame_pointer = register 5.
         * For local variables offset needs to be negative.
         * For parameters to the function it needs to be positive.
         * 
         */

        assert !global;
        return offset * 4;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal() {
        global = true;
    }

}
