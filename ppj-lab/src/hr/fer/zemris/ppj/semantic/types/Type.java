package hr.fer.zemris.ppj.semantic.types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fhrenic
 */
public abstract class Type {

    public static final VoidType VOID = new VoidType();

    public static final NumericType INT = new IntType();
    public static final NumericType CHAR = new CharType();
    public static final NumericType CONST_INT = new ConstType(INT);
    public static final NumericType CONST_CHAR = new ConstType(CHAR);

    //    public static final ArrayType ARRAY_INT = new ArrayType(INT);
    //    public static final ArrayType ARRAY_CHAR = new ArrayType(CHAR);
    //    public static final ArrayType ARRAY_CONST_INT = new ArrayType(CONST_INT);
    //    public static final ArrayType ARRAY_CONST_CHAR = new ArrayType(CONST_CHAR);

    public static final Map<Type, NumericType> CONST;
    //    public static final Map<Type, ArrayType> ARRAY;

    static {
        CONST = new HashMap<>();
        CONST.put(INT, CONST_INT);
        CONST.put(CONST_INT, CONST_INT);
        CONST.put(CHAR, CONST_CHAR);
        CONST.put(CONST_CHAR, CONST_CHAR);

        //        ARRAY = new HashMap<>();
        //        ARRAY.put(INT, ARRAY_CONST_INT);
        //        ARRAY.put(CONST_INT, ARRAY_CONST_INT);
        //        ARRAY.put(CHAR, ARRAY_CONST_CHAR);
        //        ARRAY.put(CONST_CHAR, ARRAY_CONST_CHAR);
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
