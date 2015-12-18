package hr.fer.zemris.ppj.semantic.types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhrenic
 */
public class ListType extends Type {

    private List<Type> types;

    public ListType(Type type) {
        types = new ArrayList<>();
        addType(type);
    }

    public void addType(Type type) {
        types.add(type);
    }

    public Type getType(int idx) {
        return types.get(idx);
    }

    public boolean isVoid() {
        return types.size() == 1 && types.get(0) == Type.VOID;
    }

    public boolean eachImplicit(Type toType) {
        for (Type t : types) {
            if (!t.implicit(toType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean implicitNonRef(Type toType) {
        if (toType instanceof ListType) {
            ListType lt = (ListType) toType;
            int n = types.size();
            if (lt.types.size() != n) {
                return false;
            }
            for (int idx = 0; idx < n; idx++) {
                if (!types.get(idx).implicit(lt.types.get(idx))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean same(Type t) {
        if (!(t instanceof ListType)) {
            return false;
        }
        ListType lt = (ListType) t;
        int n = types.size();
        if (lt.types.size() != n) {
            return false;
        }
        for (int idx = 0; idx < n; idx++) {
            if (!types.get(idx).same(lt.types.get(idx))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Type t : types) {
            sb.append(t);
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ')');
        return sb.toString();
    }

}
