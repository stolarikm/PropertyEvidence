package common;

/**
 * Exception thrown when an error occurs while working with database.
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class DatabaseFaultException extends RuntimeException {
    /**
     * Creates a new instance of DatabaseFaultException.
     */
    public DatabaseFaultException() {
    }

    /**
     * Creates a new instance of DatabaseFaultException, with message explaining cause.
     *
     * @param msg message explaining cause of exception.
     */
    public DatabaseFaultException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of DatabaseFaultException, with message and exception explaining cause.
     *
     * @param message   message explaining cause of exception.
     * @param exception exception that caused this exception
     */
    public DatabaseFaultException(String message, Throwable exception) {
        super(message, exception);
    }
}
