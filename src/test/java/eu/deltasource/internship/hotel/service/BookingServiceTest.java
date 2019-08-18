package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.InvalidArgumentException;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.BedType;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
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

    private BookingRepository bookingRepository;
    private GuestRepository guestRepository;
    private RoomRepository roomRepository;
    private RoomService roomService;
    private GuestService guestService;
    private BookingService bookingService;
    private Booking firstBooking;
    private Booking secondBooking;

    @BeforeEach
    public void setUp() {
        bookingRepository = new BookingRepository();
        guestRepository = new GuestRepository();
        roomRepository = new RoomRepository();
        roomService = new RoomService(roomRepository);
        guestService = new GuestService(guestRepository);
        bookingService = new BookingService(bookingRepository, roomService, guestService);
    }

    @Test
    public void findBookingByExistingId() {
        //given
        createBookings();
        int bookingId = firstBooking.getBookingId();

        //when
        Booking booking = bookingService.findById(bookingId);

        // then
        assertEquals(booking, bookingService.findById(bookingId));
    }

    @Test
    public void findBookingByIdThatDoesNotExist() {
        //given
        createBookings();
        int bookingId = -5;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
    }

    @Test
    public void deleteByExistingId() {
        //given
        createBookings();
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
        createBookings();
        int bookingId = 56;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.deleteById(bookingId));
    }

    @Test
    public void deleteExistingBooking() {
        //given
        createBookings();
        int bookingId = firstBooking.getBookingId();

        //when
        boolean result = bookingService.delete(firstBooking);

        //then
        assertTrue(result);
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
    }

    @Test
    public void deleteBookingThatDoesNotExist() {
        //given
        createBookings();
        LocalDate thirdFrom = LocalDate.of(2019, 8, 22);
        LocalDate thirdTo = LocalDate.of(2019, 8, 26);
        Booking thirdBooking = new Booking(3, 1, 1, 1, thirdFrom, thirdTo);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.delete(thirdBooking));
    }

    @Test
    public void deleteAllExistingBookings() {
        // given
        createBookings();

        //when
        bookingService.deleteAll();

        //then
        assertTrue(bookingService.findAll().isEmpty());
    }

    @Test
    public void createBookingSuccessfully() {
        //given
        createBookings();
        int bookingId = 3;
        int guestId = 1;
        int roomId = 1;
        int numberOfPeople = 2;
        LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
        LocalDate thirdTo = LocalDate.of(2019, 10, 8);
        Booking newBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);
        int expectedBookingsSize = 3;

        // when
        bookingService.save(newBooking);

        // then
        assertEquals(newBooking, bookingService.findById(bookingId));
        assertThat(bookingService.findAll(), hasSize(expectedBookingsSize));
    }

    @Test
    public void createBookingUnsuccessfully() {
        //given
        createBookings();
        LocalDate from = LocalDate.of(2019, 10, 13);
        LocalDate to = LocalDate.of(2019, 10, 22);
        Booking booking = new Booking(1, 1, 1, 1, from, to);

        // when and then
        assertThrows(BookingOverlappingException.class, () -> bookingService.save(booking));
    }

    @Test
    public void createBookingNullCheck() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> bookingService.save(null));
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
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.saveAll(firstBooking, secondBooking));
    }

    @Test
    public void findAllExistingBookings() {
        // given
        createBookings();
        int expectedSize = 2;

        //when
        int actualSize = bookingService.findAll().size();

        assertEquals(expectedSize, actualSize);
        assertThat(bookingService.findAll(), containsInAnyOrder(firstBooking, secondBooking));
    }

    @Test
    public void updateBookingByExistingRoomId() {
        //given
        createBookings();
        // from double to king size
        int numberOfPeople = 2, roomId = 3, bookingId = 1, guestId = 1;
        LocalDate from = LocalDate.of(2019, 9, 15);
        LocalDate to = LocalDate.of(2019, 9, 18);
        Booking booking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);

        //when
        bookingService.updateBooking(bookingId, booking);
        Booking updatedBooking = bookingService.findById(roomRepository.count());

        //then
        assertEquals(booking.getRoomId(), updatedBooking.getRoomId());
    }

    @Test
    public void updateBookingByRoomIdThatDoesNotExistOrIsBookedForThisPeriod() {
        //given
        createBookings();
        //create a third booking for the second room
        int numberOfPeople = 1, roomId = 2, bookingId = 3, invalidBookingId = 12, guestId = 2;
        LocalDate fromDate = LocalDate.of(2019, 9, 22);
        LocalDate toDate = LocalDate.of(2019, 9, 27);
        Booking thirdBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, fromDate, toDate);
        bookingService.save(thirdBooking);

        LocalDate from = LocalDate.of(2019, 9, 13);
        LocalDate to = LocalDate.of(2019, 9, 24);
        Booking updatedBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);

        //when and then
        // overlapping
        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBooking(3, updatedBooking));
        // invalid room id
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateBooking(invalidBookingId, updatedBooking));

    }

    @Test
    public void updateBookingByNumOfPeopleSuccessfullyAndUnsuccessfully() {
        //given
        createBookings();
        int bookingId = 1, guestId = 1, numOfPeople = 1, roomId = 1;
        LocalDate from = LocalDate.of(2019, 9, 15);
        LocalDate to = LocalDate.of(2019, 9, 18);
        Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);
        Booking invalidBooking = new Booking(bookingId, guestId, roomId, 12, from, to);

        //when
        bookingService.updateBooking(bookingId, booking);
        Booking updatedBooking = bookingService.findById(bookingRepository.count());

        //then
        assertEquals(booking.getNumberOfPeople(), updatedBooking.getNumberOfPeople());
        // no room with such capacity
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateBooking(bookingId, invalidBooking));
    }

    @Test
    public void updateBookingByDatesSuccessfully() {
        // given
        createBookings();
        LocalDate updateFrom = LocalDate.of(2019, 8, 24);
        LocalDate updateTo = LocalDate.of(2019, 8, 28);

        //when
        Booking updatedBookingByDates = bookingService.updateBookingByDates(firstBooking.getBookingId(), updateFrom, updateTo);

        assertEquals(updateFrom, updatedBookingByDates.getFrom());
        assertEquals(updateTo, updatedBookingByDates.getTo());
    }

    @Test
    public void updateBookingByDatesThrowsExceptionBecauseDatesAreOverlapped() {
        // given
        createBookings();
        LocalDate updateFrom = LocalDate.of(2019, 9, 16);
        LocalDate updateTo = LocalDate.of(2019, 9, 25);

        //when and then
        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBookingByDates(secondBooking.getBookingId(), updateFrom, updateTo));
    }

    @AfterEach
    public void tearDown() {
        roomService = null;
        guestService = null;
        bookingRepository = null;
        bookingService = null;
    }

    private void createBookings() {
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
        roomService.saveAll(doubleRoom, singleRoom, kingSizeRoom);

        LocalDate firstFrom = LocalDate.of(2019, 10, 12);
        LocalDate firstTo = LocalDate.of(2019, 10, 17);
        firstBooking = new Booking(1, 1, 1, 2, firstFrom, firstTo);

        LocalDate secondFrom = LocalDate.of(2019, 9, 18);
        LocalDate secondTo = LocalDate.of(2019, 9, 21);
        secondBooking = new Booking(2, 2, 2, 1, secondFrom, secondTo);

        // adds the bookings dto the repository which then can be accessed from BookingService
        bookingService.saveAll(firstBooking, secondBooking);
    }
}