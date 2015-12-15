package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class ArrayType extends PrimitiveType {

    private NumericType type;

    public ArrayType(NumericType type) {
        this.type = type;
    }

    public NumericType getType() {
        return type;
    }

    @Override
    public boolean isConst() {
        return type.isConst();
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        if (toType instanceof ArrayType) {
            // niz(X) ~ niz(X)
            // niz(T) ~ niz(const(T))
            NumericType ptype = ((ArrayType) toType).type;
            if (type == INT) {
                return ptype == CONST_INT;
            } else if (type == CHAR) {
                return ptype == CONST_CHAR;
            }
        }
        return false;
    }

}
