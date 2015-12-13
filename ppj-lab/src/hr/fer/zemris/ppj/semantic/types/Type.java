package hr.fer.zemris.ppj.semantic.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fhrenic
 */
public abstract class Type {

    public static final VoidType VOID = new VoidType();

    public static final NumericType INT = new IntType(false);
    public static final NumericType CHAR = new CharType(false);
    public static final NumericType CONST_INT = new IntType(true);
    public static final NumericType CONST_CHAR = new CharType(true);

    public static final ArrayType ARRAY_INT = new ArrayType(INT);
    public static final ArrayType ARRAY_CHAR = new ArrayType(CHAR);
    public static final ArrayType ARRAY_CONST_INT = new ArrayType(CONST_INT);
    public static final ArrayType ARRAY_CONST_CHAR = new ArrayType(CONST_CHAR);

    public static final Map<Type, Type> CONST;

    static {
        CONST = new HashMap<>();
        CONST.put(INT, CONST_INT);
        CONST.put(CONST_INT, CONST_INT);
        CONST.put(CHAR, CONST_CHAR);
        CONST.put(CONST_CHAR, CONST_CHAR);
    }

    public boolean explicit(Type toType) {
        return this instanceof NumericType && toType instanceof NumericType;
    }

    public boolean implicit(Type toType) {
        if (this == toType) {
            return true;
        }
        return implicitNonRef(toType);
    }

    protected abstract boolean implicitNonRef(Type toType);

}
