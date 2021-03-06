import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import hr.fer.zemris.ppj.semantic.SemNodeV;
import hr.fer.zemris.ppj.semantic.analysis.SemanticAnalyzer;
import hr.fer.zemris.ppj.util.input.SemanticInputParser;

/**
 * @author fhrenic
 */
public class SemanticTester {

    private static final String PROGRAM = "program.ppjc";
    private static final String LEX_IN = "lex.in";
    private static final String SYN_IN = "syn.in";
    private static final String LEX2SYN = "lex2syn.ppj";
    private static final String SYN2SEM = "syn2sem.ppj";

    private static String getFileName(String fileName) {
        return "semantic_test/" + fileName;
    }

    public static void main(String[] args) throws IOException {
        generateSemInput();
        semanticAnalysis();
    }

    private static void semanticAnalysis() throws IOException {
        FileInputStream fis = new FileInputStream(new File(getFileName(SYN2SEM)));
        SemanticInputParser sip = new SemanticInputParser();
        sip.parse(fis);
        SemNodeV tree = sip.getRoot();
        SemanticAnalyzer sema = new SemanticAnalyzer("prod_bnf.txt");
        sema.analysis(tree);
        if (sema.getError() != null) {
            System.out.println(sema.getError());
        } else {
            System.out.println(sema.getCodeGen());
        }

    }

    private static void generateSemInput() throws IOException {

        // lexical
        //        FileInputStream lexIn = new FileInputStream(new File(getFileName(LEX_IN)));
        //        new GLA(lexIn).generateLA();
        FileInputStream program = new FileInputStream(new File(getFileName(PROGRAM)));
        FileOutputStream lex2syn_out = new FileOutputStream(new File(getFileName(LEX2SYN)));
        new LA(program, lex2syn_out).lexicalAnalysis();

        // syntax
        //        FileInputStream synIn = new FileInputStream(new File(getFileName(SYN_IN)));
        //        new GSA(synIn).generateSA();
        FileInputStream lex2syn_in = new FileInputStream(new File(getFileName(LEX2SYN)));
        FileOutputStream syn2sem_out = new FileOutputStream(new File(getFileName(SYN2SEM)));
        new SA(lex2syn_in, syn2sem_out).syntaxAnalysis();

        System.err.println("Done");
    }

}
