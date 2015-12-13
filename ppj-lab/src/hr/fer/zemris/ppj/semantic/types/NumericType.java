package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public abstract class NumericType extends PrimitiveType {

    public abstract int getLow();

    public abstract int getTop();

    public abstract int memoryInBits();

    public boolean isInRange(int value) {
        return value >= getLow() && value <= getTop();
    }

    public final boolean isConst() {
        return this instanceof ConstType;
    }

}
