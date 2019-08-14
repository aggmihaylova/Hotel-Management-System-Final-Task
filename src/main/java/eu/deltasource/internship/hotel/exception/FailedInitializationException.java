package eu.deltasource.internship.hotel.exception;

/**
 * Represents exception for invalid data
 */
public class FailedInitializationException extends RuntimeException {

    public FailedInitializationException() {
    }

    public FailedInitializationException(String message) {
        super(message);
    }

    public FailedInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
