package hr.fer.zemris.ppj.semantic.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fhrenic
 */
public abstract class Type {

    public static final VoidType VOID = new VoidType();

    public static final IntType INT = new IntType();
    public static final CharType CHAR = new CharType();
    public static final ConstType CONST_INT = new ConstType(INT);
    public static final ConstType CONST_CHAR = new ConstType(CHAR);

    public static final ArrayType A_INT = new ArrayType(INT);
    public static final ArrayType A_CHAR = new ArrayType(CHAR);
    public static final ArrayType A_CONST_INT = new ArrayType(CONST_INT);
    public static final ArrayType A_CONST_CHAR = new ArrayType(CONST_CHAR);

    private static final Map<NumericType, ConstType> CONST;
    private static final Map<NumericType, ArrayType> ARRAY;

    static {
        CONST = new HashMap<>();
        CONST.put(INT, CONST_INT);
        CONST.put(CONST_INT, CONST_INT);
        CONST.put(CHAR, CONST_CHAR);
        CONST.put(CONST_CHAR, CONST_CHAR);

        ARRAY = new HashMap<>();
        ARRAY.put(INT, A_INT);
        ARRAY.put(CONST_INT, A_CONST_INT);
        ARRAY.put(CHAR, A_CHAR);
        ARRAY.put(CONST_CHAR, A_CONST_CHAR);
    }

    public static ConstType getConst(Type t) {
        return CONST.get(t);
    }

    public static ArrayType getArray(Type t) {
        return ARRAY.get(t);
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

    public boolean same(Type t) {
        return this == t;
    }

}
