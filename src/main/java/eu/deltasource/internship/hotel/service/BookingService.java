package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents services for bookings
 */
@Service
public class BookingService {
	private final BookingRepository bookingRepository;
	private final RoomService roomService;
	private final GuestService guestService;

	/**
	 * All arguments constructor
	 */
	@Autowired
	public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
		this.bookingRepository = bookingRepository;
		this.roomService = roomService;
		this.guestService = guestService;
	}

	/**
	 * Returns a list of all bookings for all rooms
	 */
	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	/**
	 * Gets a booking by its ID
	 *
	 * @param id dto be searched by
	 * @return Booking object that matches the given id
	 * @throws ItemNotFoundException If the Id doesn't exist in the database
	 */
	public Booking findById(int id) {
		if (bookingRepository.existsById(id)) {
			return bookingRepository.findById(id);
		}
		throw new ItemNotFoundException("There are no bookings with that ID!");
	}

	/**
	 * Creates new booking
	 *
	 * @param booking the new booking
	 */
	public Booking save(Booking booking) {
		validateBooking(booking);
		validateBookingCreationDates(booking.getFrom(), booking.getTo(), booking.getRoomId());
		bookingRepository.save(booking);
		return findById(bookingRepository.count());
	}

	/**
	 * Saves a list of booking objects
	 *
	 * @param bookings The list we want dto save
	 */
	public List<Booking> saveAll(List<Booking> bookings) {
		for (Booking booking : bookings) {
			save(booking);
		}
		return findAll();
	}

	/**
	 * Creates multiple bookings
	 *
	 * @param bookings array of bookings
	 */
	public List<Booking> saveAll(Booking... bookings) {
		for (Booking booking : bookings) {
			save(booking);
		}
		return findAll();
	}

	/**
	 * Updates booking by either room id or number of people
	 *
	 * @param bookingId  id of the booking that will be updated
	 * @param newBooking the new booking
	 * @throws ItemNotFoundException if the booking we wish dto update
	 *                               doesn't match any existing ones
	 */
	public void updateBooking(int bookingId, Booking newBooking) {
		if (bookingRepository.existsById(bookingId)) {
			validateBooking(newBooking);

			int roomId = newBooking.getRoomId();
			int guestId = newBooking.getGuestId();
			int numOfPeople = newBooking.getNumberOfPeople();
			LocalDate from = newBooking.getFrom();
			LocalDate to = newBooking.getTo();

			validateUpdateBooking(newBooking, bookingId);
			deleteById(bookingId);
			save(new Booking(bookingId, guestId, roomId, numOfPeople, from, to));
		} else {
			throw new ItemNotFoundException("Booking with id " + bookingId + " does not exist!");
		}
	}

	/**
	 * Updates booking by dates
	 *
	 * @param bookingId booking's id that will be updated
	 * @param from      starting date
	 * @param to        ending date
	 * @return updated booking
	 * @throws BookingOverlappingException if the desired dates are not free
	 */
	public Booking updateBookingByDates(int bookingId, LocalDate from, LocalDate to) {
		validateDates(from, to);
		Booking booking = findById(bookingId);

		if (validateBookingUpdateDates(from, to, booking.getRoomId(), bookingId)) {
			booking.setBookingDates(from, to);
			return bookingRepository.updateDates(booking);
		}
		throw new BookingOverlappingException("Overlapping dates!");
	}

	/**
	 * Deletes booking
	 *
	 * @param booking the booking that will be deleted
	 * @return true if the booking is successfully deleted
	 */
	public boolean delete(Booking booking) {
		validateBooking(booking);
		return bookingRepository.delete(findById(booking.getBookingId()));
	}

	/**
	 * Deletes booking by id
	 *
	 * @param id booking's id
	 */
	public boolean deleteById(int id) {
		if (bookingRepository.existsById(id)) {
			return bookingRepository.deleteById(id);
		}
		throw new ItemNotFoundException("Booking with id " + id + " does not exist!");
	}

	/**
	 * Deletes every single booking from the repo
	 */
	public void deleteAll() {
		bookingRepository.deleteAll();
	}

	private void validateUpdateBooking(Booking booking, int bookingId) {
		if (booking.getGuestId() != findById(bookingId).getGuestId()) {
			throw new ArgumentNotValidException("You are not allowed dto change guest id!");
		}
		if (!validateBookingUpdateDates(booking.getFrom(), booking.getTo(), booking.getRoomId(), bookingId)) {
			throw new BookingOverlappingException("The room is already booked for this period!");
		}
	}

	private boolean validateBookingUpdateDates(LocalDate from, LocalDate to, int roomId, int bookingId, int... idToIgnore) {

		for (Booking booking : findAll()) {
			if (booking.getRoomId() == roomId) {
				if (idToIgnore.length > 0 && booking.getBookingId() == idToIgnore[0]) {
					continue;
				}
				if (bookingId == booking.getBookingId()) {
					return (validateBookingUpdateDates(from, to, roomId, bookingId, bookingId));
				}
				if (!from.isBefore(booking.getTo()) || !to.isAfter(booking.getFrom())) {
					continue;
				}
				return false;
			}
		}
		return true;

	}


	private void validateBooking(Booking booking) {
		if (booking == null || !validateBookingFields(booking)) {
			throw new ArgumentNotValidException("Invalid Booking!");
		}
	}

	private boolean validateBookingFields(Booking booking) {
		int roomId = booking.getRoomId(), numberOfPeople = booking.getNumberOfPeople(), guestId = booking.getGuestId();
		LocalDate from = booking.getFrom(), to = booking.getTo();

		validateDates(from, to);

		return (roomService.getRoomById(roomId).getRoomId() == roomId &&
			roomService.getRoomById(roomId).getRoomCapacity() >= numberOfPeople &&
			guestService.findById(guestId).getGuestId() == guestId);
	}

	private void validateDates(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {
			throw new ArgumentNotValidException("Invalid dates!");
		}
	}

	private void validateBookingCreationDates(LocalDate from, LocalDate to, int roomId) {
		for (Booking book : findAll()) {
			if (book.getRoomId() == roomId) {
				if ((from.isBefore(book.getFrom()) && to.equals(book.getFrom()) ||

					(from.isBefore(book.getFrom()) && (to.isBefore(book.getFrom()))))) {
					continue;
				}
				if (!to.isAfter(book.getFrom()) || !from.isBefore(book.getTo())) {
					continue;
				} else {
					throw new BookingOverlappingException("The booking can not be created because dates are overlapped!");
				}
			}
		}
	}
}
