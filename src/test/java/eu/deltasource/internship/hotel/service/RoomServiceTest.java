package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
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
		Set<AbstractCommodity> singleSet = new HashSet<>
			(Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));
		singleRoom = new Room(roomId, singleSet);
		roomService.saveRoom(singleRoom);

		//when
		Room searchedRoom = roomService.getRoomById(roomId);

		//then
		assertEquals(singleRoom, searchedRoom);
	}

	@Test
	public void getRoomByIdThatDoesNotExist() {
		//given
		int roomId = 7;

		//when and then
		assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomId));
	}

	@Test
	public void getAllExistingRooms() {
		//given
		// 2 rooms already exist
		Set<AbstractCommodity> singleSet = new HashSet<>
			(Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

		Set<AbstractCommodity> kingSizeSet = new HashSet<>
			(Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

		singleRoom = new Room(1, singleSet);
		kingSizeRoom = new Room(2, kingSizeSet);

		roomService.saveRooms(singleRoom, kingSizeRoom);
		int numberOfRooms = 2;

		//when
		List<Room> rooms = roomService.findRooms();

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
		Room doubleBed = new Room(roomId, commodities);

		//when
		roomService.saveRoom(doubleBed);

		//then
		assertTrue(roomService.findRooms().contains((doubleBed)));
	}

	@Test
	public void createRoomUnsuccessfully() {
		//given
		Set<AbstractCommodity> invalidSet = null;
		Set<AbstractCommodity> doubleSet = new HashSet<>();
		int roomId = 3;
		Room kingSizeRoom = null;

		//when and then
		assertThrows(FailedInitializationException.class,
			() -> roomService.saveRoom(new Room(roomId, doubleSet)));
		assertThrows(ArgumentNotValidException.class,
			() -> roomService.saveRoom((kingSizeRoom)));
		assertThrows(FailedInitializationException.class,
			() -> roomService.saveRoom(new Room(roomId, invalidSet)));
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

		roomService.saveRooms(singleRoom, kingSizeRoom);
		int roomId = 2;

		// when
		boolean result = roomService.deleteRoomById(roomId);

		//then
		assertTrue(result);
		assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomId));
	}

	@Test
	public void deleteRoomByIdThatDoesNotExist() {
		//given
		int roomId = 12;

		//when and then
		assertThrows(ItemNotFoundException.class, () -> roomService.deleteRoomById(roomId));
	}

	@Test
	public void deleteRoomNullCheck() {
		// given
		Room room = null;

		//when and then
		assertThrows(ArgumentNotValidException.class, () -> roomService.deleteRoom(room));
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

		roomService.saveRooms(kingSizeRoom, singleRoom);

		int roomId = 1;

		//when
		boolean actualResult = roomService.deleteRoom(kingSizeRoom);

		//then
		assertTrue(actualResult);
		assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomId));
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

		roomService.saveRooms(singleRoom, kingSizeRoom);

		// when
		roomService.deleteAll();
		List<Room> allRooms = roomService.findRooms();

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

		roomService.saveRooms(singleRoom, kingSizeRoom);

		Set<AbstractCommodity> updatedCommodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Toilet()));
		int roomId = 2;
		Room updatedRoom = new Room(roomId, updatedCommodities);

		// when
		Room expectedRoom = roomService.updateRoom(updatedRoom);

		//then
		assertTrue(roomService.getRoomById(roomId).getCommodities().equals(expectedRoom.getCommodities()));
	}

	@Test
	public void updateRoomUnsuccessfully() {
		// given
		Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
			(Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

		roomService.saveRoom(new Room(1, threePeopleKingSizeSet));

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
		assertThrows(ArgumentNotValidException.class, () -> roomService.updateRoom(newRoom));
		// room with null commodity
		assertThrows(ArgumentNotValidException.class, () -> roomService.updateRoom(updatedRoomHasNullCommodity));
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
		assertThrows(ArgumentNotValidException.class, () -> roomService.saveRooms(rooms));
		//room is null
		assertThrows(ArgumentNotValidException.class, () -> roomService.saveRooms(kingSizeRoom, invalidRoom));
	}

	@Test
	public void saveRoomsSuccessfully() {
		//given
		Set<AbstractCommodity> firstRoomCommodities = new HashSet<>
			(Arrays.asList(new Bed(DOUBLE), new Toilet()));
		Set<AbstractCommodity> secondRoomCommodities = new HashSet<>
			(Arrays.asList(new Bed(SINGLE), new Bed(KING_SIZE)));
		int roomID = 1, roomId = 2, expectedSize = 2;
		Room kingSizeRoom = new Room(roomID, firstRoomCommodities);
		Room singleKingSizeRoom = new Room(roomId, secondRoomCommodities);

		//when
		roomService.saveRooms(kingSizeRoom, singleKingSizeRoom);

		//then
		assertEquals(expectedSize, roomService.findRooms().size());
		assertTrue(roomService.findRooms().contains(kingSizeRoom));
		assertTrue(roomService.findRooms().contains(singleKingSizeRoom));
	}

	@AfterEach
	public void tearDown() {
		roomRepository = null;
		roomService = null;
		singleRoom = null;
		kingSizeRoom = null;
	}
}
