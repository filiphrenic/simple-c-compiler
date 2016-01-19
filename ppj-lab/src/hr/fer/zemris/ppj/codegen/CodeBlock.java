package hr.fer.zemris.ppj.codegen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CodeBlock {

    private String name;
    private List<Code> codes;
    private Map<String, String> labels;
    private String lastLabel;
    private String nextLabel;

    public CodeBlock() {
        this("");
    }

    public CodeBlock(String name) {
        codes = new LinkedList<>();
        labels = new HashMap<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(Code code) {
        if (nextLabel != null) {
            code.setLabel(nextLabel);
            nextLabel = null;
        }
        codes.add(code);
    }

    public void labelNext(String label) {
        if(nextLabel != null) codes.add(new Code(nextLabel, new Command("")));
        nextLabel = label;
        
    }

    public void consume(CodeBlock block) {
        for (Code code : block.codes) {
            add(code);
        }
        block.codes.clear();
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
