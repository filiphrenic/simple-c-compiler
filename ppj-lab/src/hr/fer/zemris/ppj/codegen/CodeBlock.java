package hr.fer.zemris.ppj.codegen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CodeBlock {

    private List<Code> codes;
    private Map<String, String> labels;
    private String lastLabel;

    public CodeBlock() {
        codes = new LinkedList<>();
        labels = new HashMap<>();
    }

    public void add(Code code) {
        codes.add(code);
    }

    public void addLabel(String name, String label) {
        labels.put(name, label);
        lastLabel = label;
    }

    public String getLabel(String name) {
        return labels.get(name);
    }

    public String lastLabel() {
        return lastLabel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Code code : codes) {
            sb.append(code);
            sb.append('\n');
        }
        return sb.toString();
    }

    public void labelFirst(String label) {
        codes.get(0).setLabel(label);
    }

}
