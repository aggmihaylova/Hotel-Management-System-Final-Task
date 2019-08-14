package eu.deltasource.internship.hotel.exception;

/**
 * Represents exception for missing arguments
 */
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
