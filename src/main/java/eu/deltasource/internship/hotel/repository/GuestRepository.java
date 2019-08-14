package eu.deltasource.internship.hotel.repository;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents repository for guests
 */
@Repository
public class GuestRepository {

	private final List<Guest> repository;

	/**
	 * Default constructor, which initializes the repository
	 * as an empty ArrayList.
	 */
	public GuestRepository() {
		repository = new ArrayList<>();
	}

	/**
	 * Returns an unmodifiable list of all items
	 * currently in the repository.
	 */
	public List<Guest> findAll() {
		return Collections.unmodifiableList(repository);
	}

	/**
	 * Method, which checks the repository if
	 * there is an item available with the given id.
	 * <p>
	 * Check this always, before using operations with id's.
	 */
	public boolean existsById(int id) {
		for (Guest item : repository) {
			if (item.getGuestId() == id)
				return true;
		}
		return false;
	}

	/**
	 * Returns a copy of the item from the repository
	 * with the given Id.
	 */
	public Guest findById(int id) {
		for (Guest item : repository) {
			if (item.getGuestId() == id)
				return new Guest(item);
		}
		throw new ItemNotFoundException("A Guest with id: " + id + " was not found!");
	}

	private int idGenerator() {
		if (count() == 0) {
			return count() + 1;
		}
		return repository.get(count() - 1).getGuestId() + 1;
	}

	public void save(Guest item) {
		Guest newGuest = new Guest(idGenerator(), item.getFirstName(), item.getLastName(), item.getGender());
		repository.add(newGuest);
	}

	/**
	 * Saves the list of items in the repository
	 */
	public void saveAll(List<Guest> items) {
		items.forEach(
			this::save);
	}

	/**
	 * Saves all given items in the repository
	 */

	public void saveAll(Guest... items) {
		saveAll(Arrays.asList(items));
	}

	public Guest updateGuest(Guest item) {
		for (Guest guest : repository) {
			if (guest.getGuestId() == item.getGuestId()) {
				guest.changeGender(item.getGender());
				guest.changeFirstAndLastNames(item.getFirstName(), item.getLastName());
				return new Guest(guest);
			}
		}
		throw new ItemNotFoundException("Guest not found in repository!");
	}

	/**
	 * Removes an item from the repository
	 * by searching for an exact match.
	 * <p>
	 * Returns true if an exact match is and deleted,
	 * returns false if there's no match and the list is unchanged.
	 */
	public boolean delete(Guest item) {
		return repository.remove(item);
	}

	public boolean deleteById(int id) {
		for (Guest guest : repository) {
			if (guest.getGuestId() == id) {
				return delete(guest);
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
