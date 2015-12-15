package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public abstract class PrimitiveType extends Type {

    public boolean isConst() {
        return false;
    }
}
