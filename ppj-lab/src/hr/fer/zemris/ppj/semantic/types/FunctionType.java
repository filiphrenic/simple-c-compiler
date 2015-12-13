package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class FunctionType extends Type {

    private PrimitiveType returnType;
    private ListType parameterTypes;

    /**
     * @return the returnType
     */
    public PrimitiveType getReturnType() {
        return returnType;
    }

    /**
     * @return the parameterTypes
     */
    public ListType getParameterTypes() {
        return parameterTypes;
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        if (toType instanceof FunctionType) {
            FunctionType ft = (FunctionType) toType;
            return returnType.implicit(ft.returnType) && parameterTypes.implicit(ft.parameterTypes);
        }
        return false;
    }
}
