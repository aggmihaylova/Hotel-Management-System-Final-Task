package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Guest;

import eu.deltasource.internship.hotel.exception.InvalidArgumentException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Represents services for a guest
 */
@Service
public class GuestService {

    private final GuestRepository guestRepository;

    /**
     * This is a constructor
     *
     * @param guestRepository the guest repository
     */
    @Autowired
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /**
     * Gets a list of all guests
     *
     * @return list of all existing guests
     */
    public List<Guest> findAll() {
        return guestRepository.findAll();
    }

    /**
     * Searches guest by id
     *
     * @param id guest's id
     * @return copy the found guest object
     **/
    public Guest findById(int id) {
        if (!guestRepository.existsById(id)) {
            throw new ItemNotFoundException("Guest with id " + id + " does not exist!");
        }
        return guestRepository.findById(id);
    }

    /**
     * Creates a guest
     *
     * @param guest the new guest
     * @return the new added guest
     */
    public Guest save(Guest guest) {
        validateGuest(guest);
        guestRepository.save(guest);
        return findById(guestRepository.count());
    }

    /**
     * Creates a list of guests
     *
     * @param guests the list of guests
     * @return list of all existing guests
     */
    public List<Guest> saveAll(List<Guest> guests) {
        validateGuestList(guests);
        guestRepository.saveAll(guests);
        return findAll();
    }

    /**
     * Creates one or several guests
     *
     * @param guests array of guests
     * @return list of all existing guests
     */
    public List<Guest> saveAll(Guest... guests) {
        validateGuestList(Arrays.asList(guests));
        guestRepository.saveAll(guests);
        return findAll();
    }

    /**
     * Updates an existing guest
     *
     * @param guest the guest that is going to be updated
     * @return the updated guest
     */
    public Guest update(Guest guest) {
        validateGuest(guest);
        findById(guest.getGuestId());
        return guestRepository.updateGuest(guest);
    }

    /**
     * Deletes a guest by id
     *
     * @param id guest's id
     * @return true if the guest is successfully deleted
     */
    public boolean deleteById(int id) {
        if (!guestRepository.existsById(id)) {
            throw new ItemNotFoundException("Guest with id " + id + " does not exist!");
        }
        return guestRepository.deleteById(id);
    }

    /**
     * Deletes a guest
     *
     * @param guest the guest that is going to be deleted
     * @return true if the guest is successfully deleted
     */
    public boolean delete(Guest guest) {
        validateGuest(guest);
        return guestRepository.delete(findById(guest.getGuestId()));
    }

    /**
     * Deletes all existing guests
     */
    public void deleteAll() {
        guestRepository.deleteAll();
    }

    private void validateGuestList(List<Guest> guests) {
        if (guests.isEmpty()) {
            throw new InvalidArgumentException("Empty list of guests!");
        }
        for (Guest guest : guests) {
            validateGuest(guest);
        }
    }

    private void validateGuest(Guest guest) {
        if (guest == null) {
            throw new InvalidArgumentException("Invalid guest!");
        }
        if (guest.getFirstName() == null || guest.getLastName() == null || guest.getGender() == null
                || guest.getFirstName().isEmpty() || guest.getLastName().isEmpty()) {
            throw new InvalidArgumentException("Invalid guest fields!");
        }
    }
}