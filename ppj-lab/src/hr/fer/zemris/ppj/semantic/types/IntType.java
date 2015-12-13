package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class IntType extends NumericType {

    @Override
    protected boolean implicitNonRef(Type toType) {
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
    public int memoryInBits() {
        return 32;
    }

}
