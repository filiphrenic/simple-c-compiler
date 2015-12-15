package hr.fer.zemris.ppj.semantic.analysis;

/**
 * @author fhrenic
 */
public class SemanticFunctionException extends RuntimeException {

    private static final long serialVersionUID = 5185490575676134479L;

    private String functionName;

    public SemanticFunctionException(String functionName, String message) {
        super(message);
        this.functionName = functionName;
    }

    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }

}
