import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Helper class used for testing
 * 
 * @author fhrenic
 */
public abstract class Tester {

    public static void main(String[] args) throws IOException {
        lexTester.generateAnalyzeCompare(false);
        sinTester.generateAnalyzeCompare(true);
    }

    private Path dir;
    private String gen_in;
    private String ana_in;
    private String ana_out;
    private String my_out;
    private boolean verbose;

    public Tester(String dir, String gen_in, String ana_in, String ana_out, String my_out) {
        this.dir = Paths.get(dir);
        this.gen_in = gen_in;
        this.ana_in = ana_in;
        this.ana_out = ana_out;
        this.my_out = my_out;
        verbose = true;
    }

    private void maybe(String message) {
        if (verbose) {
            output(message);
        }
    }

    private void output(String message) {
        System.out.println(message);
    }

    public void generateAnalyzeCompare(boolean verbose) throws IOException {
        this.verbose = verbose;
        long start = System.currentTimeMillis();
        boolean allGood = true;
        int n = 0;

        long t1, t2;
        for (File test : dir.toFile().listFiles()) {
            n++;

            FileInputStream gfis = new FileInputStream(get_path(test, gen_in).toFile());
            FileInputStream afis = new FileInputStream(get_path(test, ana_in).toFile());
            FileOutputStream afos = new FileOutputStream(get_path(test, my_out).toFile());

            maybe("Performing " + test);

            maybe("\tGenerating...");
            t1 = System.currentTimeMillis();
            generate(gfis);
            t2 = System.currentTimeMillis();
            maybe(String.format("\tTook %.2f seconds", (t2 - t1) / 1000.0));

            maybe("\tAnalyzing...");
            t1 = System.currentTimeMillis();
            analyze(afis, afos);
            t2 = System.currentTimeMillis();
            maybe(String.format("\tTook %.2f seconds", (t2 - t1) / 1000.0));

            List<String> out = readFile(test, ana_out);
            List<String> my = readFile(test, my_out);
            boolean ok = my.equals(out);
            allGood &= ok;
            maybe("\tSame outputs? " + ok + "\n");
        }

        long end = System.currentTimeMillis();

        output(String.format("Total time for %d tests: %.2f seconds", n, (end - start) / 1000.0));
        output("All ok: " + allGood);
    }

    private List<String> readFile(File folder_name, String file_name) throws IOException {
        return Files.readAllLines(get_path(folder_name, file_name), StandardCharsets.UTF_8);
    }

    private Path get_path(File test_folder, String file_name) {
        return dir.resolve(test_folder.getName()).resolve(file_name);
    }

    public abstract void generate(FileInputStream in);

    public abstract void analyze(FileInputStream in, FileOutputStream out);

    private static final Tester lexTester = new Tester("tests_lexical", "test.lan", "test.in",
            "test.out", "my.out") {
        @Override
        public void generate(FileInputStream in) {
            new GLA(in).generateLA();
        }

        @Override
        public void analyze(FileInputStream in, FileOutputStream out) {
            new LA(in, out).lexicalAnalysis();
        }
    };

    private static final Tester sinTester = new Tester("tests_syntax", "test.san", "test.in",
            "test.out", "my.out") {
        @Override
        public void generate(FileInputStream in) {
            new GSA(in).generateSA();
        }

        @Override
        public void analyze(FileInputStream in, FileOutputStream out) {
            new SA(in, out).syntaxAnalysis();
        }
    };

}
