package hr.fer.zemris.ppj.sintax;

import java.util.Map;

/**
 * @author fhrenic
 */
public class LRParser {

    // stanje -> ( znak -> akcija )
    // Akcija i NovoStanje su objedinjeni u ovoj
    // zato jer nemaju presjeka, Akcija je definirana za stanje i završni,
    // a NovoStanje je definirana za stanje i nezavršni znak
    private Map<Integer, Map<Symbol, LRAction>> actions;

}
