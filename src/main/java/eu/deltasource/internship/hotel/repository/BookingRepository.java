package eu.deltasource.internship.hotel.repository;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents repository for bookings
 */
@Repository
public class BookingRepository {

	private final List<Booking> repository;

	/**
	 * Default constructor, which initializes the repository
	 * as an empty ArrayList.
	 */
	public BookingRepository() {
		repository = new ArrayList<>();
	}

	/**
	 * Returns an unmodifiable list of all items
	 * currently in the repository.
	 */
	public List<Booking> findAll() {
		return Collections.unmodifiableList(repository);
	}

	/**
	 * Method, which checks the repository if
	 * there is an item available with the given id.
	 * <p>
	 * Check this always, before using operations with id's.
	 */
	public boolean existsById(int id) {
		for (Booking item : repository) {
			if (item.getBookingId() == id)
				return true;
		}
		return false;
	}

	/**
	 * Returns an item from the repository
	 */
	public Booking findById(int id) {
		for (Booking item : repository) {
			if (item.getBookingId() == id)
				return new Booking(item);
		}
		throw new ItemNotFoundException("A booking with id: " + id + " was not found!");
	}

	private int idGenerator() {
		if (count() == 0) {
			return count() + 1;
		}
		return repository.get(count() - 1).getBookingId() + 1;
	}

	public void save(Booking item) {
		Booking newBooking = new Booking(idGenerator(), item.getGuestId(), item.getRoomId(),
			item.getNumberOfPeople(), item.getFrom(), item.getTo());
		repository.add(newBooking);
	}

	/**
	 * Saves the list of items in the repository
	 */
	public void saveAll(List<Booking> items) {
		items.forEach(
			this::save);
	}

	/**
	 * Saves all given items in the repository
	 */
	public void saveAll(Booking... items) {
		saveAll(Arrays.asList(items));
	}

	public Booking updateDates(Booking item) {
		for (Booking booking : repository) {
			if (booking.getBookingId() == item.getBookingId()) {
				booking.setBookingDates(item.getFrom(), item.getTo());
				return new Booking(booking);
			}
		}
		throw new ItemNotFoundException("Booking not found in repository!");
	}

	/**
	 * Removes an item from the repository
	 * by searching for an exact match.
	 * <p>
	 * Returns true if an exact match is and deleted,
	 * returns false if there's no match and the list is unchanged.
	 */
	public boolean delete(Booking item) {
		return repository.remove(item);
	}

	public boolean deleteById(int id) {
		for (Booking booking : repository) {
			if (booking.getBookingId() == id) {
				return delete(booking);
			}
		}
		return false;
	}

	/**
	 * Deletes all items in the repository
	 */
	public void deleteAll() {
		repository.clear();
	}

	/**
	 * Returns the number of items left in the repository
	 */
	public int count() {
		return repository.size();
	}
}
