
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
 * @author fhrenic
 */
public class Tester {

    public void generateFiles() throws IOException {
        String path = "tests";
        Path dir = Paths.get(path);
        for (File f : dir.toFile().listFiles()) {
            Path test = dir.resolve(f.getName());

            GLA gla = new GLA(new FileInputStream(test.resolve("test.lan").toFile()));
            gla.generateLA();

            LA la = new LA(new FileInputStream(test.resolve("test.in").toFile()),
                    new FileOutputStream(test.resolve("my.out").toFile()));
            la.lexicalAnalysis();
        }
    }

    public void testIt() throws IOException {
        String path = "tests";
        Path dir = Paths.get(path);
        boolean krivo = false;
        for (File f : dir.toFile().listFiles()) {
            List<String> out = Files.readAllLines(dir.resolve(f.getName()).resolve("test.out"),
                    StandardCharsets.UTF_8);
            List<String> my = Files.readAllLines(dir.resolve(f.getName()).resolve("my.out"),
                    StandardCharsets.UTF_8);
            if (!my.equals(out)) {
                System.err.println("Krivi je: " + f.getName());
                krivo = true;
            }
        }
        if (!krivo) {
            System.out.println("NISTA nije krivo!!!");
        }
    }

    public static void main(String[] args) throws IOException {
        Tester t = new Tester();
        t.generateFiles();
        t.testIt();
    }

}
