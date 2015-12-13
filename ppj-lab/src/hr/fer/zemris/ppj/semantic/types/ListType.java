package hr.fer.zemris.ppj.semantic.types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhrenic
 */
public class ListType extends Type {

    private List<PrimitiveType> types;

    public ListType(PrimitiveType type) {
        types = new ArrayList<>();
        addType(type);
    }

    public void addType(PrimitiveType type) {
        types.add(type);
    }

    public boolean isVoid() {
        return types.size() == 1 && types.get(0) == Type.VOID;
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

}
