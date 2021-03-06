package cz.muni.fi.pv168.backend.common;


/**
 * This exception is thrown when validation of entity fails.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class ValidationException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>ValidationException</code> without detail message.
     */
    public ValidationException() {
    }

    /**
     * Constructs an instance of
     * <code>ValidationException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}
