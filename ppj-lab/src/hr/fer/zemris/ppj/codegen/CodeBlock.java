package hr.fer.zemris.ppj.codegen;

import java.util.LinkedList;
import java.util.List;

public class CodeBlock {

    private List<Code> codes;

    public CodeBlock() {
        codes = new LinkedList<>();
    }

    public void addCode(Code code) {
        codes.add(code);
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
