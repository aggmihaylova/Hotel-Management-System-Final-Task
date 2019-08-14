package eu.deltasource.internship.hotel.exception;


public class ArgumentNotValidException extends RuntimeException {
	public ArgumentNotValidException() {
	}

	public ArgumentNotValidException(String message) {
		super(message);
	}
}
