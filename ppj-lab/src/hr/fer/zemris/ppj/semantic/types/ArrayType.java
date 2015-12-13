package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class ArrayType extends Type {
    private PrimitiveType type;

    public ArrayType(PrimitiveType type) {
        this.type = type;
    }

    public PrimitiveType getType() {
        return type;
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        if (toType instanceof ArrayType) {
            PrimitiveType ptype = ((ArrayType) toType).type;
            if (type == ptype) {
                return true;
            }
            if (type == INT) {
                return ptype == CONST_INT;
            } else if (type == CHAR) {
                return ptype == CONST_CHAR;
            }
        }
        return false;
    }
}
