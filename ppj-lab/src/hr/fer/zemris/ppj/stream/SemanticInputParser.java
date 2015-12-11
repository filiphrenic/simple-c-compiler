package hr.fer.zemris.ppj.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import hr.fer.zemris.ppj.semantic.SemNode;
import hr.fer.zemris.ppj.semantic.SemNodeT;
import hr.fer.zemris.ppj.semantic.SemNodeV;

/**
 * @author fhrenic
 */
public class SemanticInputParser {

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream(new File("tests_syntax/kanonska/test.out"));
        SemanticInputParser sip = new SemanticInputParser();
        sip.parse(is);
        System.out.println(sip.getRoot());
    }

    private SemNodeV root;

    public void parse(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        Stack<StackEntry> stack = new Stack<>();

        int indent;
        String line = reader.readLine();
        root = new SemNodeV(line);
        stack.push(new StackEntry(0, root));

        while ((line = reader.readLine()) != null) {
            indent = 0;
            while (line.charAt(indent) == ' ') {
                indent++;
            }

            while (stack.peek().indent >= indent) {
                stack.pop();
            }
            String nodeDef = line.substring(indent);
            if (nodeDef.equals("$")) {
                continue;
            }

            if (nodeDef.charAt(0) != '<') {
                String[] parts = nodeDef.split(" ", 3);
                int lineNumber = Integer.parseInt(parts[1]);
                SemNode child = new SemNodeT(parts[0], lineNumber, parts[2]);
                stack.peek().node.addChild(child);

            } else {
                SemNodeV child = new SemNodeV(nodeDef);
                stack.peek().node.addChild(child);
                stack.push(new StackEntry(indent, child));
            }
        }

    }

    public SemNodeV getRoot() {
        return root;
    }

    private static class StackEntry {
        int indent;
        SemNodeV node;

        public StackEntry(int indent, SemNodeV node) {
            this.indent = indent;
            this.node = node;
        }
    }
}
