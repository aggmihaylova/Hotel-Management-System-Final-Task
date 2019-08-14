package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.AbstractCommodity;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.Shower;
import eu.deltasource.internship.hotel.domain.commodity.Toilet;
import eu.deltasource.internship.hotel.dto.AbstractCommodityDTO;
import eu.deltasource.internship.hotel.dto.BedDTO;
import eu.deltasource.internship.hotel.dto.ToiletDTO;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;

import eu.deltasource.internship.hotel.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for the
 * logic of room actions -
 * adding, updating, deleting
 * and getting rooms
 */
@Service
public class RoomService {

	private final RoomRepository roomRepository;

	/**
	 * This is a constructor
	 *
	 * @param roomRepository rooms repository
	 */
	@Autowired
	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	/**
	 * Returns a list of all the rooms - if there are any
	 */
	public List<Room> findRooms() {
		return roomRepository.findAll();
	}

	/**
	 * Searches room by id
	 *
	 * @param id room's id
	 * @return copy of the found room
	 */
	public Room getRoomById(int id) {
		if (roomRepository.existsById(id)) {
			return roomRepository.findById(id);
		}
		throw new ItemNotFoundException("Room with id " + id + " does not exist!");
	}

	/**
	 * Creates a new room
	 *
	 * @param room the new room
	 * @return the new room
	 */
	public Room saveRoom(Room room) {
		validateRoom(room);
		roomRepository.save(room);
		return getRoomById(roomRepository.count());
	}

	/**
	 * Creates multiple rooms
	 *
	 * @param rooms array of rooms
	 * @return the new rooms
	 */
	public List<Room> saveRooms(Room... rooms) {
		validateRoomList(rooms);
		roomRepository.saveAll(rooms);
		return findRooms();
	}

	/**
	 * Creates all the rooms in the passed list of rooms
	 *
	 * @param rooms
	 * @return list of the new created rooms
	 */
	public List<Room> saveRooms(List<Room> rooms) {
		validateRoomList(rooms.toArray(new Room[rooms.size()]));
		roomRepository.saveAll(rooms);
		return findRooms();
	}

	/**
	 * Updates existing room
	 *
	 * @param room the room that will be updated
	 * @return the updated room
	 */
	public Room updateRoom(Room room) {
		validateRoom(room);
		getRoomById(room.getRoomId());
		return roomRepository.updateRoom(room);
	}

	/**
	 * Deletes room by id
	 *
	 * @param id room's id
	 * @return true if the room is successfully deleted/removed
	 */
	public boolean deleteRoomById(int id) {
		if (roomRepository.existsById(id)) {
			return roomRepository.deleteById(id);
		}
		throw new ItemNotFoundException("Room with id " + id + " does not exist!");
	}

	/**
	 * Deletes room
	 *
	 * @param room the room that will be deleted
	 * @return true if the room was successfully deleted
	 */
	public boolean deleteRoom(Room room) {
		validateRoom(room);
		return roomRepository.delete(getRoomById(room.getRoomId()));
	}

	/**
	 * Deletes all rooms
	 */
	public void deleteAll() {
		roomRepository.deleteAll();
	}

	/**
	 * Converts DTO object to model
	 *
	 * @param roomsDTO list of DTO objects
	 * @return list of model objects
	 */
	public List<Room> convertDTO(List<RoomDTO> roomsDTO) {
		List<Room> rooms = new ArrayList<>();
		for (RoomDTO room : roomsDTO) {
			rooms.add(convertDTO(room));
		}
		return rooms;
	}

	/**
	 * Converts DTO object to model
	 *
	 * @param room DTO object
	 * @return model object
	 */
	public Room convertDTO(RoomDTO room) {
		if (room == null || room.getCommodities() == null || room.getCommodities().contains(null)) {
			throw new ArgumentNotValidException("Invalid room transfer object!");
		}
		int roomId = room.getRoomId();
		Set<AbstractCommodity> roomCommodities = new HashSet<>();
		for (AbstractCommodityDTO commodityDTO : room.getCommodities()) {
			if (commodityDTO instanceof BedDTO) {
				Bed bed = new Bed(((BedDTO) commodityDTO).getBedType());
				roomCommodities.add(bed);
			} else if (commodityDTO instanceof ToiletDTO) {
				roomCommodities.add(new Toilet());
			} else {
				roomCommodities.add(new Shower());
			}
		}
		return new Room(roomId, roomCommodities);
	}

	private void validateRoomList(Room... rooms) {
		if (rooms == null) {
			throw new ArgumentNotValidException("Invalid rooms !");
		}
		for (Room room : rooms) {
			validateRoom(room);
		}
	}

	private void validateRoom(Room room) {
		if (room == null || room.getCommodities() == null
			|| room.getCommodities().isEmpty() || room.getCommodities().contains(null)) {
			throw new ArgumentNotValidException("Invalid room !");
		}
	}
}
