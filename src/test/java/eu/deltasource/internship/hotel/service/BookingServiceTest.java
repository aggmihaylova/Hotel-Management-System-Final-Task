package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.BedType;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.SINGLE;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

	private BookingRepository bookingRepository = new BookingRepository();
	private GuestRepository guestRepository = new GuestRepository();
	private RoomRepository roomRepository = new RoomRepository();
	private RoomService roomService = new RoomService(roomRepository);
	private GuestService guestService = new GuestService(guestRepository);
	private BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
	private Booking firstBooking;
	private Booking secondBooking;

	@BeforeEach
	public void setUp() {

		// guests
		Guest firstGuest = new Guest(1, "John", "Miller", Gender.MALE);
		Guest secondGuest = new Guest(2, "Maria", "Tam", Gender.FEMALE);
		guestService.saveAll(firstGuest, secondGuest);

		// Commodities for a double room
		Set<AbstractCommodity> doubleSet = new HashSet<>(Arrays.asList
			(new Bed(BedType.DOUBLE), new Toilet(), new Shower()));

		// commodities for a single room
		Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList
			(new Bed(SINGLE), new Toilet(), new Shower()));

		// commodities for a double room with king size bed
		Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList
			(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

		// create some rooms
		Room doubleRoom = new Room(1, doubleSet);
		Room singleRoom = new Room(2, singleSet);
		Room kingSizeRoom = new Room(3, kingSizeSet);

		// adds the rooms dto the repository which then can be accessed from  RoomService
		roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom);

		LocalDate firstFrom = LocalDate.of(2019, 8, 15);
		LocalDate firstTo = LocalDate.of(2019, 8, 18);
		firstBooking = new Booking(1, 1, 1, 2, firstFrom, firstTo);

		LocalDate secondFrom = LocalDate.of(2019, 9, 18);
		LocalDate secondTo = LocalDate.of(2019, 9, 21);
		secondBooking = new Booking(2, 2, 2, 1, secondFrom, secondTo);

		// adds the bookings dto the repository which then can be accessed from BookingService
		bookingService.saveAll(firstBooking, secondBooking);
	}

	@Test
	public void findBookingByExistingId() {
		//given
		// two bookings already exist
		int bookingId = firstBooking.getBookingId();

		//when
		Booking booking = bookingService.findById(bookingId);

		// then
		assertEquals(booking, bookingService.findById(bookingId));
	}

	@Test
	public void findBookingByIdThatDoesNotExist() {
		//given
		// two bookings already exist
		int bookingId = -5;

		//when and then
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
	}

	@Test
	public void deleteByExistingId() {
		//given
		// two bookings already exist
		int bookingId = 2;

		//when
		boolean actualResult = bookingService.deleteById(bookingId);

		// then
		assertTrue(actualResult);
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
	}

	@Test
	public void deleteByIdThatDoesNotExist() {
		//given
		// two bookings already exist
		int bookingId = 56;

		//when and then
		assertThrows(ItemNotFoundException.class, () -> bookingService.deleteById(bookingId));
	}

	@Test
	public void deleteExistingBooking() {
		//given
		// two bookings already exist
		int bookingId = firstBooking.getBookingId(), size = 1;

		//when
		boolean result = bookingService.delete(firstBooking);

		//then
		assertTrue(result);
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
		assertEquals(size, bookingService.findAll().size());
	}

	@Test
	public void deleteBookingThatDoesNotExist() {
		//given
		// two bookings already exist
		int guestId = 1, bookingId = 3, roomId = 1, numberOfPeople = 1;
		LocalDate thirdFrom = LocalDate.of(2019, 8, 22);
		LocalDate thirdTo = LocalDate.of(2019, 8, 26);
		Booking thirdBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);

		//when and then
		assertThrows(ItemNotFoundException.class, () -> bookingService.delete(thirdBooking));
	}

	@Test
	public void deleteAllExistingBookings() {
		// given
		// two bookings already exist
		int firstBookingId = 1, secondBookingId = 2;

		//when
		bookingService.deleteAll();

		//then
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(firstBookingId));
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(secondBookingId));
	}

	@Test
	public void createBookingSuccessfully() {
		//given
		// two bookings already exist
		int bookingId = 3, guestId = 1, roomId = 1, numberOfPeople = 2;
		LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
		LocalDate thirdTo = LocalDate.of(2019, 10, 8);
		Booking newBooking = new Booking
			(bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);
		int expectedBookingsSize = 3;

		// when
		bookingService.save(newBooking);

		// then
		assertEquals(newBooking, bookingService.findById(bookingId));
		assertEquals(expectedBookingsSize, bookingService.findAll().size());
	}

	@Test
	public void createBookingUnsuccessfully() {
		//given
		// two bookings already exist
		int bookingId = 3, guestId = 3, roomId = 7, numberOfPeople = 2;
		LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
		LocalDate thirdTo = LocalDate.of(2019, 10, 8);
		Booking thirdBooking = new Booking
			(bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);

		LocalDate from = LocalDate.of(2019, 8, 13);
		LocalDate to = LocalDate.of(2019, 8, 22);
		Booking fourthBooking = new Booking(1, 1, 1, 1, from, to);

		// when and then
		// overlapping
		assertThrows(BookingOverlappingException.class, () -> bookingService.save(fourthBooking));

		//invalid room id
		assertThrows(ItemNotFoundException.class,
			() -> bookingService.save(thirdBooking));

		// booking is null
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.save(null));
	}

	@Test
	public void saveAllBookingSuccessfully() {
		//given
		// new bookings
		BookingRepository bookingRepository = new BookingRepository();
		BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
		int firstBookingId = 1, firstGuestId = 2, firstRoomId = 3, firstNumOfPeople = 2;
		int secondBookingId = 2, secondGuestId = 1, secondRoomId = 2, secondNumOfPeople = 1;
		int expectedSize = 2;
		LocalDate firstFrom = LocalDate.of(2019, 12, 3);
		LocalDate firstTo = LocalDate.of(2019, 12, 6);
		LocalDate secondFrom = LocalDate.of(2019, 12, 13);
		LocalDate secondTo = LocalDate.of(2019, 12, 16);
		Booking firstBooking = new Booking(firstBookingId, firstGuestId, firstRoomId, firstNumOfPeople, firstFrom, firstTo);
		Booking secondBooking = new Booking(secondBookingId, secondGuestId, secondRoomId, secondNumOfPeople, secondFrom, secondTo);

		//when
		bookingService.saveAll(firstBooking, secondBooking);

		//then
		assertEquals(expectedSize, bookingService.findAll().size());
		assertEquals(firstBooking, bookingService.findById(firstBookingId));
		assertEquals(secondBooking, bookingService.findById(secondBookingId));
	}

	@Test
	public void saveAllBookingUnsuccessfully() {
		//given
		BookingRepository bookingRepository = new BookingRepository();
		BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
		int firstBookingId = 1, firstGuestId = 2, firstRoomId = 3, firstNumOfPeople = 12;
		int secondBookingId = 2, secondGuestId = 1, secondRoomId = 7, secondNumOfPeople = 1;
		LocalDate firstFrom = LocalDate.of(2019, 12, 3);
		LocalDate firstTo = LocalDate.of(2019, 12, 6);
		LocalDate secondFrom = LocalDate.of(2019, 12, 13);
		LocalDate secondTo = LocalDate.of(2019, 12, 16);
		Booking firstBooking = new Booking
			(firstBookingId, firstGuestId, firstRoomId, firstNumOfPeople, firstFrom, firstTo);
		Booking secondBooking = new Booking
			(secondBookingId, secondGuestId, secondRoomId, secondNumOfPeople, secondFrom, secondTo);

		//when and then
		//not enough capacity for the first booking and invalid room id for the second
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.saveAll(firstBooking, secondBooking));
	}

	@Test
	public void findAllExistingBookings() {
		// given
		//two bookings already exist
		int expectedSize = 2;

		//when
		int actualSize = bookingService.findAll().size();

		assertEquals(expectedSize, actualSize);
		assertThat("The repository does not contain all bookings!",
			bookingService.findAll(), containsInAnyOrder(firstBooking, secondBooking));
	}

	@Test
	public void updateBookingByExistingRoomId() {
		//given
		// from double dto king size
		int numberOfPeople = 2, roomId = 3, bookingId = 1, guestId = 1;
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking
			(bookingId, guestId, roomId, numberOfPeople, from, to);

		//when
		bookingService.updateBooking(bookingId, booking);
		Booking updatedBooking = bookingService.findById(roomRepository.count());

		//then
		assertEquals(booking.getRoomId(), updatedBooking.getRoomId());
	}

	@Test
	public void updateBookingByRoomIdThatDoesNotExistOrIsBookedForThisPeriod() {
		//given
		// two bookings already exist
		//create a third booking for the second room
		int numberOfPeople = 1, roomId = 2, bookingId = 3, invalidBookingId = 12, guestId = 2;
		LocalDate fromDate = LocalDate.of(2019, 8, 14);
		LocalDate toDate = LocalDate.of(2019, 8, 19);
		Booking thirdBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, fromDate, toDate);
		bookingService.save(thirdBooking);

		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking updatedBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);

		//when and then
		// overlapping
		assertThrows(BookingOverlappingException.class,
			() -> bookingService.updateBooking(2, updatedBooking));
		// invalid room id
		assertThrows(ItemNotFoundException.class,
			() -> bookingService.updateBooking(invalidBookingId, updatedBooking));

	}

	@Test
	public void updateBookingByNumOfPeopleSuccessfully() {
		//given
		// two rooms already exist
		int bookingId = 1, guestId = 1, numOfPeople = 1, roomId = 1;
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);

		//when
		bookingService.updateBooking(bookingId, booking);
		Booking updatedBooking = bookingService.findById(bookingRepository.count());

		//then
		assertEquals(booking.getNumberOfPeople(), updatedBooking.getNumberOfPeople());
	}

	@Test
	public void updateBookingByNumOfPeopleUnsuccessfully() {
		//given
		int bookingId = 1, guestId = 1, numOfPeople = 12, roomId = 1;
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);

		//when and then
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.updateBooking(bookingId, booking));
	}

	@Test
	public void updateBookingByDatesSuccessfully() {
		// given
		// two bookings already exist
		int bookingId = 1;
		LocalDate updateFrom = LocalDate.of(2019, 8, 24);
		LocalDate updateTo = LocalDate.of(2019, 8, 28);

		//when
		Booking findBooking = bookingService.updateBookingByDates(bookingId, updateFrom, updateTo);

		assertEquals(updateFrom, findBooking.getFrom());
		assertEquals(updateTo, findBooking.getTo());
	}

	@Test
	public void updateBookingByDatesUnsuccessfully() {
		// given
		// two bookings already exist
		int bookingId = 1, guestId = 1, numOfPeople = 2, roomId = 1;
		LocalDate from = LocalDate.of(2019, 8, 19);
		LocalDate to = LocalDate.of(2019, 8, 27);
		Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);
		bookingService.save(booking);

		LocalDate updateFrom = LocalDate.of(2019, 8, 22);
		LocalDate updateTo = LocalDate.of(2019, 8, 26);

		//when and then
		assertThrows(BookingOverlappingException.class, () -> bookingService.updateBookingByDates(bookingId, updateFrom, updateTo));
	}
}
