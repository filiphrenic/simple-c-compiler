package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */
public class SemNode {

    private String name;

    public SemNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        return spaces(indent) + repr(indent);
    }

    public String repr(int indent) {
        return "";
    }

    public static String spaces(int numberOfSpaces) {
        return new String(new char[numberOfSpaces]).replace('\0', ' ');
    }

}
