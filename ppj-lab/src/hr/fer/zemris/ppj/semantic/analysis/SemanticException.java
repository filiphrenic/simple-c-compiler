package hr.fer.zemris.ppj.semantic.analysis;

import hr.fer.zemris.ppj.semantic.SemNode;

/**
 * @author fhrenic
 */
public class SemanticException extends RuntimeException {

    private static final long serialVersionUID = -754000325203012425L;

    private SemNode node;

    public SemanticException(String message, SemNode node) {
        super(message);
        this.node = node;
    }

    public SemanticException(Throwable cause, SemNode node) {
        super(cause);
        this.node = node;
    }

}
