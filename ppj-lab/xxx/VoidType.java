package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class VoidType extends PrimitiveType {

    @Override
    protected boolean implicitNonRef(Type toType) {
        return false;
    }

    @Override
    public String toString() {
        return "void";
    }

}
