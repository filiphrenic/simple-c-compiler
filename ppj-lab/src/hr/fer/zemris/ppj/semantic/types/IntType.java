package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class IntType extends NumberType {

    @Override
    protected boolean implicitNonRef(Type toType) {
        // int, const int
        return toType == CONST_INT;
    }

    @Override
    public int getLow() {
        return -(1 << 31);
    }

    @Override
    public int getTop() {
        return (1 << 31) - 1;
    }

    @Override
    public int bytes() {
        return 4;
    }

    @Override
    public String toString() {
        return "int";
    }

}
