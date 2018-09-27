package common;

/**
 * Exception thrown when entity is not valid
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class IllegalEntityException extends RuntimeException {
    /**
     * Creates a new instance of IllegalEntityException.
     */
    public IllegalEntityException() {
    }

    /**
     * Creates a new instance of IllegalEntityException, with message explaining cause.
     *
     * @param msg message explaining cause of exception.
     */
    public IllegalEntityException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of IllegalEntityException, with message and exception explaining cause.
     *
     * @param message   message explaining cause of exception.
     * @param exception exception that caused this exception
     */
    public IllegalEntityException(String message, Throwable exception) {
        super(message, exception);
    }
}
