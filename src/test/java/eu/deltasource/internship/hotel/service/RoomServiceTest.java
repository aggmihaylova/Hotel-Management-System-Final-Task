package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.InvalidArgumentException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.BedType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;
    private Room singleRoom;
    private Room kingSizeRoom;

    @BeforeEach
    public void setUp() {
        roomRepository = new RoomRepository();
        roomService = new RoomService(roomRepository);
    }

    @Test
    public void getRoomByExistingId() {
        //given
        int roomId = 1;
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Shower()));
        singleRoom = new Room(roomId, singleSet);
        roomService.save(singleRoom);

        //when
        Room searchedRoom = roomService.findById(roomId);

        //then
        assertEquals(singleRoom, searchedRoom);
        assertEquals(singleRoom.getRoomCapacity(), searchedRoom.getRoomCapacity());
        assertEquals(singleRoom.getCommodities(), searchedRoom.getCommodities());
    }

    @Test
    public void getRoomByIdThatDoesNotExist() {
        //given
        int roomId = 1;
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Shower()));
        singleRoom = new Room(roomId, singleSet);
        roomService.save(singleRoom);
        int id = 7;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.findById(id));
    }

    @Test
    public void getAllExistingRooms() {
        //given
        Set<AbstractCommodity> singleSet = new HashSet<>
                (Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        singleRoom = new Room(1, singleSet);
        kingSizeRoom = new Room(2, kingSizeSet);

        roomService.saveAll(singleRoom, kingSizeRoom);
        int numberOfRooms = 2;

        //when
        List<Room> rooms = roomService.findAll();

        //then
        assertEquals(numberOfRooms, rooms.size());
        assertTrue(rooms.contains(singleRoom));
        assertTrue(rooms.contains(kingSizeRoom));
    }

    @Test
    public void createRoomSuccessfully() {
        //given
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE)));
        int roomId = 1;
        Room doubleBedRoom = new Room(roomId, commodities);

        //when
        roomService.save(doubleBedRoom);

        //then
        assertTrue(roomService.findAll().contains((doubleBedRoom)));
    }

    @Test
    public void createRoomUnsuccessfully() {
        //given
        Set<AbstractCommodity> invalidSet = null;
        Set<AbstractCommodity> doubleSet = new HashSet<>();
        int roomId = 3;
        Room kingSizeRoom = null;

        //when and then
        // empty set of commodities
        assertThrows(FailedInitializationException.class,
                () -> roomService.save(new Room(roomId, doubleSet)));
        //the room is null
        assertThrows(InvalidArgumentException.class,
                () -> roomService.save((kingSizeRoom)));
        //invalid commodities
        assertThrows(FailedInitializationException.class,
                () -> roomService.save(new Room(roomId, invalidSet)));
    }

    @Test
    public void deleteRoomByExistingId() {
        // given
        Set<AbstractCommodity> singleSet = new HashSet<>
                (Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(DOUBLE), new Toilet(), new Shower()));

        singleRoom = new Room(1, singleSet);
        kingSizeRoom = new Room(2, kingSizeSet);

        roomService.saveAll(singleRoom, kingSizeRoom);
        int roomId = 2;

        // when
        boolean result = roomService.deleteById(roomId);

        //then
        assertTrue(result);
        assertThrows(ItemNotFoundException.class, () -> roomService.findById(roomId));
    }

    @Test
    public void deleteRoomByIdThatDoesNotExist() {
        //given
        int roomId = 1;
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Shower()));
        singleRoom = new Room(roomId, singleSet);
        roomService.save(singleRoom);
        int invalidId = 12;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.deleteById(invalidId));
    }

    @Test
    public void deleteExistingRoom() {
        //given
        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        kingSizeRoom = new Room(1, threePeopleKingSizeSet);
        singleRoom = new Room(2, kingSizeSet);

        roomService.saveAll(kingSizeRoom, singleRoom);

        //when
        boolean actualResult = roomService.delete(kingSizeRoom);

        //then
        assertTrue(actualResult);
        assertThrows(ItemNotFoundException.class, () -> roomService.findById(1));
    }

    @Test
    public void deleteAllExistingRooms() {
        //given
        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        singleRoom = new Room(1, kingSizeSet);
        kingSizeRoom = new Room(2, threePeopleKingSizeSet);

        roomService.saveAll(singleRoom, kingSizeRoom);

        // when
        roomService.deleteAll();
        List<Room> allRooms = roomService.findAll();

        //then
        assertTrue(allRooms.isEmpty());
    }

    @Test
    public void updateRoomSuccessfully() {
        // given
        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        singleRoom = new Room(1, kingSizeSet);
        kingSizeRoom = new Room(2, threePeopleKingSizeSet);

        roomService.saveAll(singleRoom, kingSizeRoom);

        Set<AbstractCommodity> updatedCommodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Toilet()));
        int roomId = 2;
        Room updatedRoom = new Room(roomId, updatedCommodities);

        // when
        Room expectedRoom = roomService.updateRoom(updatedRoom);

        //then
        assertEquals(roomService.findById(roomId).getCommodities(), expectedRoom.getCommodities());
    }

    @Test
    public void updateRoomUnsuccessfully() {
        // given
        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        roomService.save(new Room(1, threePeopleKingSizeSet));

        Set<AbstractCommodity> updatedCommodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE), new Shower()));
        Set<AbstractCommodity> updatedCommoditiesNull = new HashSet<>(Arrays.asList(new Bed(DOUBLE), null));
        int roomId = 8;
        Room updatedRoom = new Room(roomId, updatedCommodities);
        Room updatedRoomHasNullCommodity = new Room(1, updatedCommoditiesNull);
        Room newRoom = null;

        // when and then
        //invalid room id
        assertThrows(ItemNotFoundException.class, () -> roomService.updateRoom(updatedRoom));
        // room is null
        assertThrows(InvalidArgumentException.class, () -> roomService.updateRoom(newRoom));
        // room with null commodity
        assertThrows(InvalidArgumentException.class, () -> roomService.updateRoom(updatedRoomHasNullCommodity));
    }

    @Test
    public void savedRoomsUnsuccessfully() {
        //given
        Room[] rooms = null;
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Shower()));
        int roomId = 3;
        Room kingSizeRoom = new Room(roomId, commodities);
        Room invalidRoom = null;

        // when and then
        //rooms reference is null
        assertThrows(InvalidArgumentException.class, () -> roomService.saveAll(rooms));
        //room is null
        assertThrows(InvalidArgumentException.class, () -> roomService.saveAll(kingSizeRoom, invalidRoom));
    }

    @Test
    public void saveRoomsSuccessfully() {
        //given
        Set<AbstractCommodity> firstRoomCommodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE), new Toilet()));
        Set<AbstractCommodity> secondRoomCommodities = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Bed(KING_SIZE)));
        int roomID = 1;
        int roomId = 2;
        int expectedSize = 2;
        Room kingSizeRoom = new Room(roomID, firstRoomCommodities);
        Room singleKingSizeRoom = new Room(roomId, secondRoomCommodities);

        //when
        roomService.saveAll(kingSizeRoom, singleKingSizeRoom);

        //then
        assertEquals(expectedSize, roomService.findAll().size());
        assertTrue(roomService.findAll().contains(kingSizeRoom));
        assertTrue(roomService.findAll().contains(singleKingSizeRoom));
    }

    @AfterEach
    public void tearDown() {
        roomRepository = null;
        roomService = null;
    }
}