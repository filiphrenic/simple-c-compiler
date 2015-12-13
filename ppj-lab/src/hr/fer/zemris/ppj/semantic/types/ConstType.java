package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class ConstType extends NumericType {

    private NumericType type;

    protected ConstType(NumericType type) {
        this.type = type;
    }

    public NumericType getType() {
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
    public int memoryInBits() {
        return type.memoryInBits();
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        // TODO Auto-generated method stub
        return false;
    }

}
