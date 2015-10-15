/**
 * 
 */
package hr.fer.zemris.ppj.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author fhrenic
 */
public class Streamer {

    private static final String FILE_LOCATION = "";
    private static File FILE = new File(FILE_LOCATION);

    public static ObjectOutputStream getOutput() throws IOException {
        FileOutputStream fileStream = new FileOutputStream(FILE);
        ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
        return objStream;
    }

    public static ObjectInputStream getInput() throws IOException {
        FileInputStream fileStream = new FileInputStream(FILE);
        ObjectInputStream objStream = new ObjectInputStream(fileStream);
        return objStream;
    }

}
