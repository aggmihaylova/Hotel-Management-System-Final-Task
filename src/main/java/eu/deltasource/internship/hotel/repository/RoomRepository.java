package eu.deltasource.internship.hotel.repository;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Represents repository for rooms
 */
@Repository
public class RoomRepository {

	private final List<Room> repository;

	/**
	 * Default constructor, which initializes the repository
	 * as an empty ArrayList.
	 */
	public RoomRepository() {
		repository = new ArrayList<>();
	}

	/**
	 * Returns an unmodifiable list of all items
	 * currently in the repository.
	 */
	public List<Room> findAll() {
		return Collections.unmodifiableList(repository);
	}

	/**
	 * Method, which checks the repository if
	 * there is an item available with the given id.
	 * <p>
	 * Check this always, before using operations with id's.
	 */
	public boolean existsById(int id) {
		for (Room item : repository) {
			if (item.getRoomId() == id)
				return true;
		}
		return false;
	}

	/**
	 * Returns a copy of the item from the repository
	 * with the given Id.
	 */
	public Room findById(int id) {
		for (Room item : repository) {
			if (item.getRoomId() == id)
				return new Room(item);
		}
		throw new ItemNotFoundException("A Room with id: " + id + " was not found!");
	}

	private int idGenerator() {
		if (count() == 0) {
			return count() + 1;
		}
		return repository.get(count() - 1).getRoomId() + 1;
	}

	public void save(Room item) {
		Room newRoom = new Room(idGenerator(), item.getCommodities());
		repository.add(newRoom);
	}

	/**
	 * Saves the list of items in the repository
	 */
	public void saveAll(List<Room> items) {
		items.forEach(
			this::save);
	}

	/**
	 * Saves all given items in the repository
	 */
	public void saveAll(Room... items) {
		saveAll(Arrays.asList(items));
	}

	public Room updateRoom(Room item) {
		for (Room room : repository) {
			if (room.getRoomId() == item.getRoomId()) {
				room.updateCommodities(item.getCommodities());
				return new Room(room);
			}
		}
		throw new ItemNotFoundException("Room not found in repository!");
	}

	/**
	 * Removes an item from the repository
	 * by searching for an exact match.
	 * <p>
	 * Returns true if an exact match is and deleted,
	 * returns false if there's no match and the list is unchanged.
	 */
	public boolean delete(Room item) {
		return repository.remove(item);
	}

	public boolean deleteById(int id) {
		for (Room room : repository) {
			if (room.getRoomId() == id) {
				return delete(room);
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
