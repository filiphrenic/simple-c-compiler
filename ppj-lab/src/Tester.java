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
        //lexTester.run();
        sinTester.generateAnalyzeCompare();
    }

    private Path dir;
    private String gen_in;
    private String ana_in;
    private String ana_out;
    private String my_out;

    public Tester(String dir, String gen_in, String ana_in, String ana_out, String my_out) {
        this.dir = Paths.get(dir);
        this.gen_in = gen_in;
        this.ana_in = ana_in;
        this.ana_out = ana_out;
        this.my_out = my_out;
    }

    public void generateAnalyzeCompare() throws IOException {
        long t1, t2;
        for (File test : dir.toFile().listFiles()) {

            FileInputStream gfis = new FileInputStream(get_path(test, gen_in).toFile());
            FileInputStream afis = new FileInputStream(get_path(test, ana_in).toFile());
            FileOutputStream afos = new FileOutputStream(get_path(test, my_out).toFile());

            System.out.println("Performing " + test);

            System.out.println("\tGenerating...");
            t1 = System.currentTimeMillis();
            generate(gfis);
            t2 = System.currentTimeMillis();
            System.out.format("\tTook %.2f seconds\n", (t2 - t1) / 1000.0);

            System.out.println("\tAnalyzing...");
            t1 = System.currentTimeMillis();
            analyze(afis, afos);
            t2 = System.currentTimeMillis();
            System.out.format("\tTook %.2f seconds\n", (t2 - t1) / 1000.0);

            List<String> out = readFile(test, ana_out);
            List<String> my = readFile(test, my_out);
            System.out.println("\tSame outputs? " + my.equals(out));
            System.out.println();
        }
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
