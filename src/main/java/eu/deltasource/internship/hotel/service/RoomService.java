package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.AbstractCommodity;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.Shower;
import eu.deltasource.internship.hotel.domain.commodity.Toilet;
import eu.deltasource.internship.hotel.dto.*;
import eu.deltasource.internship.hotel.exception.InvalidArgumentException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;

import eu.deltasource.internship.hotel.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Represents services for a room
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * This is a constructor
     *
     * @param roomRepository room repository
     */
    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Gets a list of all the rooms - if there are any
     *
     * @return list of all existing rooms
     */
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    /**
     * Searches room by id
     *
     * @param id room's id
     * @return copy of the found room object
     */
    public Room findById(int id) {
        if (!roomRepository.existsById(id)) {
            throw new ItemNotFoundException("Room with id " + id + " does not exist!");
        }
        return roomRepository.findById(id);
    }

    /**
     * Creates a room
     *
     * @param room the new room
     * @return the new added room
     */
    public Room save(Room room) {
        validateRoom(room);
        roomRepository.save(room);
        return findById(roomRepository.count());
    }

    /**
     * Creates a list of rooms
     *
     * @param rooms the list of rooms
     * @return list of all existing rooms
     */
    public List<Room> saveAll(List<Room> rooms) {
        validateRoomList(rooms);
        roomRepository.saveAll(rooms);
        return findAll();
    }

    /**
     * Creates one or several rooms
     *
     * @param rooms array of rooms
     * @return list of all existing rooms
     */
    public List<Room> saveAll(Room... rooms) {
        validateRoomList(Arrays.asList(rooms));
        roomRepository.saveAll(rooms);
        return findAll();
    }

    /**
     * Updates an existing room
     *
     * @param room the room that is going be updated
     * @return the updated room
     */
    public Room updateRoom(Room room) {
        validateRoom(room);
        findById(room.getRoomId());
        return roomRepository.updateRoom(room);
    }

    /**
     * Deletes a room by id
     *
     * @param id room's id
     * @return true if the room is successfully deleted
     */
    public boolean deleteById(int id) {
        if (!roomRepository.existsById(id)) {
            throw new ItemNotFoundException("Room with id " + id + " does not exist!");
        }
        return roomRepository.deleteById(id);
    }

    /**
     * Deletes a room
     *
     * @param room the room that is going to be deleted
     * @return true if the room was successfully deleted
     */
    public boolean delete(Room room) {
        validateRoom(room);
        return roomRepository.delete(findById(room.getRoomId()));
    }

    /**
     * Deletes all existing rooms
     */
    public void deleteAll() {
        roomRepository.deleteAll();
    }

    /**
     * Converts DTO object to model object
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
            throw new InvalidArgumentException("Invalid room transfer object!");
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

    private void validateRoomList(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            throw new InvalidArgumentException("Invalid rooms !");
        }
        for (Room room : rooms) {
            validateRoom(room);
        }
    }

    private void validateRoom(Room room) {
        if (room == null || room.getCommodities() == null
                || room.getCommodities().isEmpty() || room.getCommodities().contains(null)) {
            throw new InvalidArgumentException("Invalid room !");
        }
    }
}