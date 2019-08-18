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

import java.util.*;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RoomServiceTest {

    private RoomService roomService;
    private Room singleRoom;
    private Room kingSizeRoom;

    @BeforeEach
    public void setUp() {
        RoomRepository roomRepository = new RoomRepository();
        roomService = new RoomService(roomRepository);
    }

    @Test
    public void getRoomByExistingId() {
        //given
        int roomId = 1;
        createRooms();

        //when
        Room searchedRoom = roomService.findById(roomId);

        //then
        assertEquals(singleRoom, searchedRoom);
    }

    @Test
    public void getRoomByIdThatDoesNotExist() {
        //given
        createRooms();
        int id = 7;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.findById(id));
    }

    @Test
    public void getAllExistingRooms() {
        //given
        createRooms();
        int numberOfRooms = 2;

        //when
        List<Room> rooms = roomService.findAll();

        //then
        assertThat(rooms, hasSize(numberOfRooms));
        assertThat(rooms, containsInAnyOrder(kingSizeRoom, singleRoom));
    }

    @Test
    public void createRoomSuccessfully() {
        //given
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE), new Shower()));
        int roomId = 1;
        Room doubleBedRoom = new Room(roomId, commodities);

        //when
        roomService.save(doubleBedRoom);

        //then
        assertTrue(roomService.findAll().contains((doubleBedRoom)));
    }

    @Test
    public void createRoomThrowsExceptionWhenTheSetIsNullOrIsEmpty() {
        //given
        Set<AbstractCommodity> invalidSet = null;
        Set<AbstractCommodity> emptySet = new HashSet<>();
        int roomId = 3;

        //when and then
        // empty set of commodities
        assertThrows(FailedInitializationException.class,
                () -> roomService.save(new Room(roomId, emptySet)));
        //invalid commodities
        assertThrows(FailedInitializationException.class,
                () -> roomService.save(new Room(roomId, invalidSet)));
    }

    @Test
    public void createRoomNullCheck() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> roomService.save(null));
    }

    @Test
    public void deleteRoomByExistingId() {
        // given
        createRooms();
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
        createRooms();
        int invalidId = 12;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.deleteById(invalidId));
    }

    @Test
    public void deleteExistingRoom() {
        //given
        int roomId = 2;
        createRooms();

        //when
        boolean actualResult = roomService.delete(kingSizeRoom);

        //then
        assertTrue(actualResult);
        assertThrows(ItemNotFoundException.class, () -> roomService.findById(roomId));
    }

    @Test
    public void deleteAllExistingRooms() {
        //given
        createRooms();

        // when
        roomService.deleteAll();
        List<Room> allRooms = roomService.findAll();

        //then
        assertTrue(allRooms.isEmpty());
    }

    @Test
    public void updateRoomSuccessfully() {
        // given
        createRooms();
        Set<AbstractCommodity> updatedCommodities = new HashSet<>
                (Arrays.asList(new Bed(KING_SIZE), new Toilet()));
        int roomId = 2;
        Room updatedRoom = new Room(roomId, updatedCommodities);

        // when
        Room expectedRoom = roomService.updateRoom(updatedRoom);

        //then
        assertEquals(updatedRoom.getCommodities(), expectedRoom.getCommodities());
    }

    @Test
    public void updateRoomThrowsExceptionWhenRoomHasNullCommodityOrUpdatesNonExistentRoom() {
        // given
        createRooms();
        Set<AbstractCommodity> updatedCommodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE), new Shower()));
        Set<AbstractCommodity> updatedCommoditiesNull = new HashSet<>(Arrays.asList(new Bed(DOUBLE), null));
        int roomId = 8;
        Room updatedRoom = new Room(roomId, updatedCommodities);
        Room updatedRoomHasNullCommodity = new Room(1, updatedCommoditiesNull);

        // when and then
        //invalid room id
        assertThrows(ItemNotFoundException.class, () -> roomService.updateRoom(updatedRoom));
        // room with null commodity
        assertThrows(InvalidArgumentException.class, () -> roomService.updateRoom(updatedRoomHasNullCommodity));
    }

    @Test
    public void saveRoomsVarargsThrowsExceptionWhenContainsNullRoom() {
        //given
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Shower()));
        int roomId = 3;
        Room kingSizeRoom = new Room(roomId, commodities);
        Room invalidRoom = null;

        // when and then
        assertThrows(InvalidArgumentException.class, () -> roomService.saveAll(kingSizeRoom, invalidRoom));
    }

    @Test
    public void saveListOfRoomSuccessfully() {
        //given
        int roomId = 1;
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Shower()));
        Room kingSizeRoom = new Room(roomId, commodities);
        List<Room> rooms = new ArrayList<>();
        rooms.add(kingSizeRoom);

        // when
        roomService.saveAll(rooms);

        //then
        assertThat(rooms, hasSize(1));
    }

    @Test
    public void saveListOfRoomThrowsExceptionWhenIsEmpty() {
        //given
        List<Room> rooms = new ArrayList<>();

        // when and then
        assertThrows(InvalidArgumentException.class, () -> roomService.saveAll(rooms));
    }

    @AfterEach
    public void tearDown() {
        roomService = null;
    }

    private void createRooms() {
        Set<AbstractCommodity> singleSet = new HashSet<>
                (Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        singleRoom = new Room(1, singleSet);
        kingSizeRoom = new Room(2, kingSizeSet);

        roomService.saveAll(singleRoom, kingSizeRoom);
    }
}