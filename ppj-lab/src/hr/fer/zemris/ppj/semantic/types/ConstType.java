package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class ConstType extends NumericType {

    private NumberType type;

    protected ConstType(NumberType type) {
        this.type = type;
    }

    public NumberType getType() {
        return type;
    }

    @Override
    public int getLow() {
        return type.getLow();
    }

    @Override
    public int getTop() {
        return type.getTop();
    }

    @Override
    public int bytes() {
        return type.bytes();
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        return type.implicit(toType);
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public String toString() {
        return "const " + type;
    }

}
