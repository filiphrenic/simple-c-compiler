/**
 * 
 */
package hr.fer.zemris.ppj.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This class is used mainly for handling input/output streams. GLA class writes
 * objects using object output stream. Those objects are read by the LA class.
 * 
 * @author fhrenic
 */
public class Streamer {

    public static void main(String[] args) {

    }

    // TODO assign input/output file
    // file used for communication between GLA and LA
    private static final String FILE_LOCATION = "./analizator/objects.ost";
    private static File FILE = new File(FILE_LOCATION);

    // reading/writing to streams
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BUFFER_CAPACITY = 1 << 10;

    /**
     * Returns an object stream used to read objects produced by the GLA
     * 
     * @return object output stream
     * @throws IOException
     */
    public static ObjectOutputStream getOutput() throws IOException {
        FileOutputStream fileStream = new FileOutputStream(FILE);
        ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
        return objStream;
    }

    /**
     * Returns an object stream used by the GLA to write objects
     * 
     * @return object input stream
     * @throws IOException
     */
    public static ObjectInputStream getInput() throws IOException {
        FileInputStream fileStream = new FileInputStream(FILE);
        ObjectInputStream objStream = new ObjectInputStream(fileStream);
        return objStream;
    }

    /**
     * Helper method that reads entire content from an input stream into a
     * single string
     * 
     * @param stream input stream to read from
     * @return stream content as a string
     * @throws IOException
     */
    public static String readFromStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int length;
        byte[] buffer = new byte[BUFFER_CAPACITY];
        while ((length = stream.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, length, CHARSET));
        }
        return sb.toString();
    }

    /**
     * Writes a given string to the output stream
     * 
     * @param output string to write
     * @param stream output stream
     * @throws IOException
     */
    public static void writeToStream(String output, OutputStream stream) throws IOException {
        stream.write(output.getBytes(CHARSET));
    }

}
