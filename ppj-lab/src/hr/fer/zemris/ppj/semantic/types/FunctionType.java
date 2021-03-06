package hr.fer.zemris.ppj.semantic.types;

/**
 * @author fhrenic
 */
public class FunctionType extends Type {

    private Type returnType;
    private ListType parameterTypes;

    public FunctionType(ListType parameterTypes, Type returnType) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    /**
     * @return the returnType
     */
    public Type getReturnType() {
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
        if (!(toType instanceof FunctionType)) {
            return false;
        }
        FunctionType ft = (FunctionType) toType;
        return returnType.implicit(ft.returnType) && parameterTypes.implicit(ft.parameterTypes);
    }

    public boolean same(Type t) {
        if (!(t instanceof FunctionType)) {
            return false;
        }
        FunctionType ft = (FunctionType) t;
        return returnType.same(ft.returnType) && parameterTypes.same(ft.parameterTypes);
    }

    @Override
    public String toString() {
        return parameterTypes + " -> " + returnType;
    }
}
