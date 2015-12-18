package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */
public class SemNodeT extends SemNode {

    private int lineNumber;
    private String value;

    /**
     * @param name
     */
    public SemNodeT(String name, int lineNumber, String value) {
        super(name);
        this.lineNumber = lineNumber;
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String represent() {
        return getName() + '(' + lineNumber + ',' + value + ')';
    }

}
