package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public abstract class NumericType extends PrimitiveType {

    private boolean isConst;

    public NumericType(boolean isConst) {
        this.isConst = isConst;
    }

    public abstract int getLow();

    public abstract int getTop();

    public abstract int memoryInBits();

    public boolean isInRange(int value) {
        return value >= getLow() && value <= getTop();
    }

    public boolean getIsConst() {
        return isConst;
    }

}
