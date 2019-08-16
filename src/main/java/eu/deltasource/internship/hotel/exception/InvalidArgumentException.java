package eu.deltasource.internship.hotel.exception;

/**
 * Represents exception when method's argument is invalid
 */
public class InvalidArgumentException extends RuntimeException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
