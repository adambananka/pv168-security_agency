package cz.muni.fi.pv168.backend.common;

/**
 * This exception indicates service failure.
 *
 * @author Adam Baňanka, Daniel Homola
 */
public class ServiceFailureException extends RuntimeException {

    /**
     * Constructs an instance of
     * <code>ServiceFailureException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public ServiceFailureException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>ServiceFailureException</code> with the specified detail
     * cause.
     *
     * @param cause the cause
     */
    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of
     * <code>ServiceFailureException</code> with the specified detail
     * message and cause.
     *
     * @param message the detail message.
     * @param cause the cause
     */
    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}