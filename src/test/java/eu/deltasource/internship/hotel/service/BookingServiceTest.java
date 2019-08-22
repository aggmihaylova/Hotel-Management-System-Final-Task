package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.*;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.*;
import eu.deltasource.internship.hotel.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
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
    public void findBookingByIdBecauseBookingIdDoesNotExist() {
        //given
        createBookings();
        int bookingId = secondBooking.getBookingId() + 1;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
    }

    @Test
    public void deleteByExistingId() {
        //given
        createBookings();
        int bookingId = secondBooking.getBookingId();

        //when
        boolean actualResult = bookingService.deleteById(bookingId);

        // then
        assertTrue(actualResult);
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
    }

    @Test
    public void deleteByIdThrowsExceptionBecauseBookingIdDoesNotExist() {
        //given
        createBookings();
        int bookingId = secondBooking.getBookingId() + 1;

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
    public void deleteBookingThrowsExceptionBecauseBookingDoesNotExist() {
        //given
        createBookings();
        int bookingId = 3;
        int guestId = 1;
        int roomId = 1;
        int numberOfPeople = 1;
        LocalDate thirdFrom = LocalDate.of(2019, Month.NOVEMBER, 22);
        LocalDate thirdTo = LocalDate.of(2019, Month.NOVEMBER, 26);
        Booking thirdBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);

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
        LocalDate from = LocalDate.of(2019, Month.OCTOBER, 22);
        LocalDate to = LocalDate.of(2019, Month.OCTOBER, 27);
        Booking newBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);
        int expectedBookingsSize = 3;

        // when
        bookingService.save(newBooking);

        // then
        assertEquals(newBooking, bookingService.findById(bookingId));
        assertThat(bookingService.findAll(), hasSize(expectedBookingsSize));
    }

    @Test
    public void createBookingThrowsExceptionBecauseDatesAreOverlapped() {
        //given
        createBookings();
        int bookingId = 1;
        int guestId = 1;
        int roomId = 1;
        int numberOfPeople = 1;
        LocalDate from = LocalDate.of(2019, Month.OCTOBER, 15);
        LocalDate to = LocalDate.of(2019, Month.OCTOBER, 22);
        Booking booking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);

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
        int firstBookingId = 1;
        int firstGuestId = 2;
        int firstRoomId = 3;
        int firstNumOfPeople = 12;
        LocalDate firstFrom = LocalDate.of(2019, 12, 3);
        LocalDate firstTo = LocalDate.of(2019, 12, 6);
        Booking firstBooking = new Booking
                (firstBookingId, firstGuestId, firstRoomId, firstNumOfPeople, firstFrom, firstTo);

        //when and then
        //not enough capacity for the first booking and invalid room id for the second
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.saveAll(firstBooking, null));
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
        int numberOfPeople = 1;
        int roomId = 2;
        int bookingId = 1;
        int guestId = 1;
        LocalDate from = LocalDate.of(2019, Month.SEPTEMBER, 15);
        LocalDate to = LocalDate.of(2019, Month.SEPTEMBER, 18);
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
        int numberOfPeople = 1;
        int roomId = 2;
        int bookingId = 3;
        int invalidBookingId = 12;
        int guestId = 2;
        LocalDate fromDate = LocalDate.of(2019, Month.SEPTEMBER, 22);
        LocalDate toDate = LocalDate.of(2019, Month.SEPTEMBER, 27);
        Booking thirdBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, fromDate, toDate);
        bookingService.save(thirdBooking);

        LocalDate from = LocalDate.of(2019, Month.SEPTEMBER, 13);
        LocalDate to = LocalDate.of(2019, Month.SEPTEMBER, 24);
        Booking updatedBooking = new Booking(bookingId, guestId, roomId, numberOfPeople, from, to);

        //when and then
        // overlapping
        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBooking(bookingId, updatedBooking));
        // invalid room id
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateBooking(invalidBookingId, updatedBooking));
    }

    @Test
    public void updateBookingByNumOfPeopleSuccessfully() {
        //given
        createBookings();
        int bookingId = 1;
        int guestId = 1;
        int numOfPeople = 1;
        int roomId = 1;
        LocalDate from = LocalDate.of(2019, Month.SEPTEMBER, 15);
        LocalDate to = LocalDate.of(2019, Month.SEPTEMBER, 18);
        Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);

        //when
        bookingService.updateBooking(bookingId, booking);
        Booking updatedBooking = bookingService.findById(bookingRepository.count());

        //then
        assertEquals(booking.getNumberOfPeople(), updatedBooking.getNumberOfPeople());
    }

    @Test
    public void updateBookingByDatesSuccessfully() {
        // given
        createBookings();
        LocalDate updateFrom = LocalDate.of(2019, Month.AUGUST, 24);
        LocalDate updateTo = LocalDate.of(2019, Month.AUGUST, 28);

        //when
        Booking updatedBookingByDates =
                bookingService.updateBookingByDates(firstBooking.getBookingId(), updateFrom, updateTo);

        assertEquals(updateFrom, updatedBookingByDates.getFrom());
        assertEquals(updateTo, updatedBookingByDates.getTo());
    }

    @Test
    public void updateBookingByDatesThrowsExceptionBecauseDatesAreOverlapped() {
        // given
        createBookings();
        LocalDate from = LocalDate.of(2019, Month.SEPTEMBER, 28);
        LocalDate to = LocalDate.of(2019, Month.SEPTEMBER, 30);
        Booking booking = new Booking(3, 1, 2, 1, from, to);
        bookingService.save(booking);

        LocalDate updateFrom = LocalDate.of(2019, Month.SEPTEMBER, 19);
        LocalDate updateTo = LocalDate.of(2019, Month.SEPTEMBER, 29);

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

        // create some rooms
        Room doubleRoom = new Room(1, doubleSet);
        Room singleRoom = new Room(2, singleSet);
        // adds the rooms dto the repository which then can be accessed from  RoomService
        roomService.saveAll(doubleRoom, singleRoom);

        LocalDate firstFrom = LocalDate.of(2019, Month.OCTOBER, 12);
        LocalDate firstTo = LocalDate.of(2019, Month.OCTOBER, 17);
        firstBooking = new Booking(1, 1, 1, 2, firstFrom, firstTo);

        LocalDate secondFrom = LocalDate.of(2019, Month.SEPTEMBER, 18);
        LocalDate secondTo = LocalDate.of(2019, Month.SEPTEMBER, 21);
        secondBooking = new Booking(2, 2, 2, 1, secondFrom, secondTo);

        // adds the bookings dto the repository which then can be accessed from BookingService
        bookingService.saveAll(firstBooking, secondBooking);
    }
}