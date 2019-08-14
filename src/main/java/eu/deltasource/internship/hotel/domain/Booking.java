package eu.deltasource.internship.hotel.domain;

import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Represents booking for a hotel room
 */
@Getter
public class Booking {

	private final int bookingId;
	private final int guestId;
	private final int roomId;
	private int numberOfPeople;
	private LocalDate from;
	private LocalDate to;

	public Booking(int bookingId, int guestId, int roomId, int numberOfPeople, LocalDate from, LocalDate to) {
		this.bookingId = bookingId;
		this.guestId = guestId;
		this.roomId = roomId;
		this.numberOfPeople = numberOfPeople;
		setBookingDates(from, to);
	}

	/**
	 * This constructor should be used
	 * only by the repository.
	 */
	public Booking(Booking booking) {
		this.bookingId = booking.bookingId;
		this.guestId = booking.guestId;
		this.roomId = booking.roomId;
		this.numberOfPeople = booking.numberOfPeople;
		setBookingDates(booking.from, booking.to);
	}

	public void setBookingDates(LocalDate from, LocalDate to) {
		try {
			if (from.isAfter(to) || to.equals(from) || from.isBefore(LocalDate.now())) {
				throw new FailedInitializationException("Invalid dates given!");
			}
			this.from = from;
			this.to = to;
		} catch (NullPointerException npe) {
			throw new FailedInitializationException("Date parameters are null!");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Booking)) {
			return false;
		}
		return bookingId == ((Booking) obj).bookingId;
	}

	@Override
	public int hashCode() {
		return bookingId;
	}
}
