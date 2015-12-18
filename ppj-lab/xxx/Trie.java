package hr.fer.zemris.ppj.semantic.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.ppj.semantic.ProductionEnum;
import hr.fer.zemris.ppj.semantic.SemNodeV;

/**
 * @author fhrenic
 */
public class Trie {

    public static final String PRODUCTIONS = "prod_bnf.txt";

    private TrieNode<String, ProductionEnum> root;

    public static void main(String[] args) {
        new Trie(PRODUCTIONS);
    }

    public Trie(String filename) {
        root = new TrieNode<>();
        ProductionEnum[] pes = ProductionEnum.values();

        try {
            
            for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)) {
                // pe_index lhs ::= [rhs]
                String[] parts = line.split(" ");
                ProductionEnum pe = pes[Integer.parseInt(parts[0])];
                addProduction(pe, parts);
            }
        } catch (NumberFormatException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public ProductionEnum findProduction(SemNodeV node) {
        TrieNode<String, ProductionEnum> curr = root.getTransition(node.getName());
        if (root == null) {
            return null;
        }
        for (int idx = 0; idx < node.numOfChildren(); idx++) {
            curr = curr.getTransition(node.getChild(idx).getName());
            if (curr == null)
                return null;
        }
        return curr.getValue();
    }

    private void addProduction(ProductionEnum pe, String[] production) {
        TrieNode<String, ProductionEnum> curr = addTransition(root, production[1]);
        for (int idx = 3; idx < production.length; idx++) {
            curr = addTransition(curr, production[idx]);
        }
        curr.setValue(pe);
    }

    private static TrieNode<String, ProductionEnum> addTransition(
            TrieNode<String, ProductionEnum> curr, String sym) {
        TrieNode<String, ProductionEnum> next = curr.getTransition(sym);
        if (next == null) {
            next = new TrieNode<>();
            curr.addTransition(sym, next);
        }
        return next;
    }

}
