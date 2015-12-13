package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class CharType extends NumericType {

    protected CharType(boolean isConst) {
        super(isConst);
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        return toType instanceof NumericType;
    }

    @Override
    public int getLow() {
        return 0;
    }

    @Override
    public int getTop() {
        return 255;
    }

    @Override
    public int memoryInBits() {
        return 8;
    }

}
