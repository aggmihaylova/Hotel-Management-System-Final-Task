package eu.deltasource.internship.hotel.domain;

import eu.deltasource.internship.hotel.service.BookingService;
import eu.deltasource.internship.hotel.service.GuestService;
import eu.deltasource.internship.hotel.service.RoomService;

/**
 * Represents a hotel
 */
public class Hotel {

	private final BookingService bookingService;

	private final GuestService guestService;

	private final RoomService roomService;

	public Hotel(BookingService bookingService, GuestService guestService, RoomService roomService) {
		this.bookingService = bookingService;
		this.guestService = guestService;
		this.roomService = roomService;
	}
}
