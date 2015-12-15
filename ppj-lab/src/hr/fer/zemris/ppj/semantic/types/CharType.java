package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class CharType extends NumberType {

    @Override
    protected boolean implicitNonRef(Type toType) {
        // int, char, const int, const char
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
