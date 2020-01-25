package edu.soumya.logstash.filter.exceptions;

/**
 * @author SOUMYA BANERJEE
 *
 */
public class ConfigurationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4818922514318829454L;

	/**
     * Constructs an {@code ConfigurationException} with {@code null}
     * as its error detail message.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Constructs an {@code ConfigurationException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ConfigurationException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     *
     * @since 1.6
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ConfigurationException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     *
     * @since 1.6
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
