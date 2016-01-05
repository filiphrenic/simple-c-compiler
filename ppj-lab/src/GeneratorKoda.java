import java.io.IOException;
import java.io.InputStream;

import hr.fer.zemris.ppj.semantic.SemNodeV;
import hr.fer.zemris.ppj.semantic.analysis.SemanticAnalyzer;
import hr.fer.zemris.ppj.util.input.SemanticInputParser;

public class GeneratorKoda {

    public static void main(String[] args) throws IOException {
        InputStream input = System.in;
        new SemantickiAnalizator().analyse(input);
    }

    public void analyse(InputStream input) throws IOException {
        SemanticInputParser sip = new SemanticInputParser();
        sip.parse(input);
        SemNodeV tree = sip.getRoot();
        SemanticAnalyzer sema = new SemanticAnalyzer("prod_bnf.txt");
        sema.analysis(tree);
        if (sema.getError() != null) {
            System.out.println(sema.getError());
        } else {
            System.out.println(sema.getCodeGen());
        }
    }
}
