package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.*;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Represents services for a booking
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final GuestService guestService;

    /**
     * This is a constructor
     *
     * @param bookingRepository the booking repository
     * @param roomService       the room service
     * @param guestService      the guest service
     */
    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    /**
     * Gets a list of all bookings
     *
     * @return list of all existing bookings
     */
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    /**
     * Searches booking by id
     *
     * @param id booking's id
     * @return copy of the found booking object
     */
    public Booking findById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new ItemNotFoundException("There are no bookings with such id!");
        }
        return bookingRepository.findById(id);
    }

    /**
     * Creates a booking
     *
     * @param booking the new booking
     * @return the new added booking
     */
    public Booking save(Booking booking) {
        validateBooking(booking);
        bookingRepository.save(booking);
        return findById(bookingRepository.count());
    }

    /**
     * Creates a list of bookings
     *
     * @param bookings the list of bookings
     * @return list of all existing bookings
     */
    public List<Booking> saveAll(List<Booking> bookings) {
        validateBookings(bookings);
        bookingRepository.saveAll(bookings);
        return findAll();
    }

    /**
     * Creates one or several bookings
     *
     * @param bookings array of bookings
     */
    public void saveAll(Booking... bookings) {
        saveAll(Arrays.asList(bookings));
    }

    /**
     * Updates booking by either room id or number of people
     *
     * @param bookingId  id of the booking that will be updated
     * @param newBooking the new booking
     */
    public void updateBooking(int bookingId, Booking newBooking) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ItemNotFoundException("Booking with id " + bookingId + " does not exist!");
        }

        bookingNullCheck(newBooking);
        validateGuest(newBooking.getGuestId());
        validateRoom(newBooking.getRoomId(), newBooking.getNumberOfPeople());
        validateUpdateBooking(newBooking, bookingId);

        deleteById(bookingId);

        save(newBooking);
    }

    /**
     * Updates booking by dates
     *
     * @param bookingId id of the booking that is going be updated
     * @param from      starting date
     * @param to        ending date
     * @return the updated booking
     **/
    public Booking updateBookingByDates(int bookingId, LocalDate from, LocalDate to) {
        validateDates(from, to);
        Booking booking = findById(bookingId);

        if (areUpdateDatesOverlapped(from, to, booking.getRoomId(), bookingId)) {
            throw new BookingOverlappingException("Overlapping dates");
        }
        booking.setBookingDates(from, to);
        return bookingRepository.updateDates(booking);
    }

    /**
     * Deletes booking
     *
     * @param booking the booking that is going to be deleted
     * @return true is the booking is successfully deleted
     */
    public boolean delete(Booking booking) {
        bookingNullCheck(booking);
        return bookingRepository.delete(findById(booking.getBookingId()));
    }

    /**
     * Deletes booking by id
     *
     * @param id booking's id
     * @return true if the booking is successfully deleted
     */
    public boolean deleteById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new ItemNotFoundException("Booking with id " + id + " does not exist!");
        }
        return bookingRepository.deleteById(id);
    }

    /**
     * Deletes all existing bookings
     */
    public void deleteAll() {
        bookingRepository.deleteAll();
    }

    private void validateUpdateBooking(Booking booking, int bookingId) {
        if (booking.getGuestId() != findById(bookingId).getGuestId()) {
            throw new InvalidArgumentException("You are not allowed to change guest id");
        }
        if (areUpdateDatesOverlapped(booking.getFrom(), booking.getTo(), booking.getRoomId(), bookingId)) {
            throw new BookingOverlappingException("The room is already booked for this period!");
        }
    }

    private boolean areUpdateDatesOverlapped(LocalDate from, LocalDate to, int roomId, int bookingId) {
        Booking bookingToBeUpdated = findById(bookingId);
        if (areDatesInTheSameRange(bookingToBeUpdated.getFrom(), bookingToBeUpdated.getTo(), from, to))
            return false;

        return areDatesOverlapped(bookingToBeUpdated.getFrom(), bookingToBeUpdated.getTo(), roomId);
    }

    private void validateBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            throw new InvalidArgumentException("Empty list of bookings");
        }
        for (Booking booking : bookings) {
            validateBooking(booking);
        }
    }

    private void validateBooking(Booking booking) {
        bookingNullCheck(booking);
        validateDates(booking.getFrom(), booking.getTo());
        validateGuest(booking.getGuestId());
        validateRoom(booking.getRoomId(), booking.getNumberOfPeople());

        if (areDatesOverlapped(booking.getFrom(), booking.getTo(), booking.getRoomId())) {
            throw new BookingOverlappingException("The booking can not be created because dates are overlapped");
        }
    }

    private void bookingNullCheck(Booking booking) {
        if (booking == null) {
            throw new InvalidArgumentException("Booking can not be null");
        }
    }

    private void validateGuest(int guestId) {
        guestService.findById(guestId);
    }

    private void validateRoom(int roomId, int numberOfPeople) {
        Room foundExistingRoom = roomService.findById(roomId);
        if (foundExistingRoom.getRoomCapacity() < numberOfPeople) {
            throw new InvalidArgumentException("The room does not have enough capacity");
        }
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {
            throw new InvalidArgumentException("Invalid dates");
        }
    }

    private boolean areDatesOverlapped(LocalDate from, LocalDate to, int roomId) {
        for (Booking book : findAll()) {
            if (book.getRoomId() == roomId && checkOverlapping(book.getFrom(), book.getTo(), from, to)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOverlapping(LocalDate bookedFrom, LocalDate bookedTo, LocalDate from, LocalDate to) {
        return !(bookedFrom.isAfter(to) || (bookedFrom.isEqual(to)) ||
                (bookedTo.isBefore(from) || bookedTo.isEqual(from)));
    }

    private boolean areDatesInTheSameRange(LocalDate bookedFrom, LocalDate bookedTo,
                                           LocalDate fromUpdated, LocalDate toUpdated) {
        return ((bookedFrom.isAfter(fromUpdated) || bookedFrom.isEqual(fromUpdated)) &&
                (bookedTo.isAfter(toUpdated) || bookedTo.isEqual(toUpdated)));
    }
}