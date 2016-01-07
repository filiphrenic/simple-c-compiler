import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hr.fer.zemris.ppj.semantic.SemNodeV;
import hr.fer.zemris.ppj.semantic.analysis.SemanticAnalyzer;
import hr.fer.zemris.ppj.util.Streamer;
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

        Object output = sema.getError();
        if (output == null) output = sema.getCodeGen();
        OutputStream stream = new FileOutputStream(new File("a.frisc"));
        Streamer.writeToStream(output, stream);
    }
}
