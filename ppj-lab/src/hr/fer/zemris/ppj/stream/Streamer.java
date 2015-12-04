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

    // names of files used for communication between generators and analyzers 
    public static final String FOLDER = "analizator";
    public static final String LEXICAL_OBJECTS = "lex_objects.ppj";
    public static final String SYNTAX_OBJECTS = "syn_objects.ppj";

    static {
        new File(FOLDER).mkdirs();
    }

    // reading/writing to streams
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BUFFER_CAPACITY = 1 << 10;

    /**
     * Returns an object stream used to read objects. Mainly used by analyzers
     * to read objects created by generators.
     * 
     * @param fileName name of the file to be used as a output stream
     * @return object output stream
     * @throws IOException
     */
    public static ObjectOutputStream getOutput(String fileName) throws IOException {
        File output = new File(fileName);
        FileOutputStream fileStream = new FileOutputStream(output);
        ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
        return objStream;
    }

    /**
     * Returns an object stream used mainly by generators to write objects
     * 
     * @param fileName name of the file to be used as a input stream
     * @return object input stream
     * @throws IOException
     */
    public static ObjectInputStream getInput(String fileName) throws IOException {
        File input = new File(fileName);
        FileInputStream fileStream = new FileInputStream(input);
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
     * @param output object to write
     * @param stream output stream
     * @throws IOException
     */
    public static void writeToStream(Object output, OutputStream stream) throws IOException {
        stream.write(output.toString().getBytes(CHARSET));
    }

    public static String getFilename4Generator(String filename) {
        return "analizator/" + filename;
    }

    public static String getFilename4Analyzer(String filename) {
        return "analizator/" + filename;
    }
}
