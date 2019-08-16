package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.InvalidArgumentException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GuestServiceTest {
    private GuestService guestService;
    private Guest firstGuest;

    @BeforeEach
    public void setUp() {
        GuestRepository guestRepository = new GuestRepository();
        guestService = new GuestService(guestRepository);
    }

    @Test
    public void getGuestByExistingId() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);

        //when
        Guest searchedGuest = guestService.findById(firstGuest.getGuestId());

        //then
        assertEquals(firstGuest, searchedGuest);
        assertEquals(firstGuest.getFirstName(), searchedGuest.getFirstName());
        assertEquals(firstGuest.getLastName(), searchedGuest.getLastName());
        assertEquals(firstGuest.getGender(), searchedGuest.getGender());
    }

    @Test
    public void getGuestByIdThatDoesNotExist() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);
        int invalidId = firstGuest.getGuestId() + 1;

        // when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(invalidId));
    }

    @Test
    public void updateGuestSuccessfully() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);
        Guest updatedGuest = new Guest(firstGuest.getGuestId(), "George", "Jordan", Gender.MALE);

        //when
        Guest actualGuest = guestService.update(updatedGuest);
        assertEquals(updatedGuest, actualGuest);

        //then
        assertEquals(updatedGuest.getFirstName(), actualGuest.getFirstName());
        assertEquals(updatedGuest.getLastName(), actualGuest.getLastName());
        assertEquals(Gender.MALE, actualGuest.getGender());
    }

    @Test
    public void updateGuestUnsuccessfully() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);
        Guest updatedGuest = new Guest(firstGuest.getGuestId() + 1, "Martin", "Miller", Gender.MALE);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.update(updatedGuest));
        assertThrows(InvalidArgumentException.class, () -> guestService.update(null));
    }

    @Test
    public void deleteGuestByExistingId() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);

        //when and then
        assertTrue(guestService.deleteById(firstGuest.getGuestId()));
    }

    @Test
    public void deleteGuestByIdThatDoesNotExist() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.deleteById(firstGuest.getGuestId() + 1));
    }

    @Test
    public void deleteExistingGuest() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);

        //when
        assertTrue(guestService.delete(firstGuest));

        //then
        assertFalse(guestService.findAll().contains(firstGuest));
    }

    @Test
    public void deleteGuestThatDoesNotExist() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        guestService.save(firstGuest);

        //when and then
        assertThrows(ItemNotFoundException.class,
                () -> guestService.delete(new Guest(firstGuest.getGuestId() + 1, "Gergana", "Todorova", Gender.FEMALE)));
    }

    @Test
    public void deleteAllExistingGuests() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        Guest newGuest = new Guest(2, "Peter", "Miller", Gender.MALE);
        guestService.saveAll(firstGuest, newGuest);

        //when
        guestService.deleteAll();

        //then
        assertTrue(guestService.findAll().isEmpty());
    }

    @Test
    public void findAllExistingGuests() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        Guest secondGuest = new Guest(2, "Martin", "Dyson", Gender.MALE);
        Guest thirdGuest = new Guest(3, "Joe", "Cunning", Gender.MALE);

        //when
        guestService.saveAll(firstGuest, secondGuest, thirdGuest);
        List<Guest> guests = guestService.findAll();

        //then
        assertThat(guests, hasSize(3));
        assertThat(guests, containsInAnyOrder(firstGuest, secondGuest, thirdGuest));
    }

    @Test
    public void findAllExistingGuestsEmptyList() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        Guest secondGuest = new Guest(2, "Martin", "Dyson", Gender.MALE);
        Guest thirdGuest = new Guest(3, "Joe", "Cunning", Gender.MALE);

        //when and then
        assertTrue(guestService.findAll().isEmpty());
    }

    @Test
    public void createGuestUnsuccessfully() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> guestService.save(null));
        assertThrows(FailedInitializationException.class, () -> guestService.save(new Guest(1, null, null, Gender.MALE)));
    }

    @Test
    public void createGuestSuccessfully() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        Guest newGuest = new Guest(2, "Mike", "Peterson", Gender.MALE);

        //when
        guestService.save(firstGuest);
        guestService.save(newGuest);

        //then
        assertEquals(firstGuest, guestService.findById(1));
        assertEquals(newGuest, guestService.findById(2));
    }

    @Test
    public void createListOfGuests() {
        //given
        firstGuest = new Guest(1, "Maria", "Johnson", Gender.FEMALE);
        Guest secondGuest = new Guest(2, "Michael", "Miller", Gender.MALE);
        Guest thirdGuest = new Guest(3, "George", "Port", Gender.MALE);
        List<Guest> guests = new ArrayList<>();
        guests.add(firstGuest);
        guests.add(secondGuest);
        guests.add(thirdGuest);

        //When
        guestService.saveAll(guests);
        List<Guest> allGuests = guestService.findAll();

        //Then
        assertThat(allGuests, hasSize(3));
        assertThat(allGuests, containsInAnyOrder(firstGuest, secondGuest, thirdGuest));
    }

    @AfterEach
    public void tearDown() {
        guestService = null;
    }
}