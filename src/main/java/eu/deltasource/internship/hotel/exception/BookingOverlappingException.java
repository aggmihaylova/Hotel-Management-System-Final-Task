package eu.deltasource.internship.hotel.exception;

/**
 * Represent exception when two dates are overlapped
 */
public class BookingOverlappingException extends RuntimeException {

    public BookingOverlappingException(String message) {
        super(message);
    }
}
