package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public abstract class NumericType extends PrimitiveType {

    public abstract int getLow();

    public abstract int getTop();

    public abstract int bytes();

    public boolean isInRange(int value) {
        return value >= getLow() && value <= getTop();
    }

}
