
package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.*;
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
        createGuest();

        //when
        Guest searchedGuest = guestService.findById(firstGuest.getGuestId());

        //then
        assertEquals(firstGuest, searchedGuest);
    }

    @Test
    public void getGuestByIdThrowsExceptionBecauseGuestIdDoesNotExist() {
        //given
        createGuest();
        int invalidId = firstGuest.getGuestId() + 1;

        // when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(invalidId));
    }

    @Test
    public void updateGuestSuccessfully() {
        //given
        createGuest();
        String firstName = "George";
        String lastName = "Jordan";
        Gender gender = Gender.MALE;
        Guest updatedGuest = new Guest(firstGuest.getGuestId(), firstName, lastName, gender);

        //when
        Guest actualGuest = guestService.update(updatedGuest);

        //then
        assertEquals(updatedGuest, actualGuest);
    }

    @Test
    public void updateGuestThrowsExceptionBecauseGuestDoesNotExist() {
        //given
        createGuest();
        int invalidId = firstGuest.getGuestId() + 1;
        Guest updatedGuest = new Guest(invalidId, "Martin", "Miller", Gender.MALE);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.update(updatedGuest));
    }

    @Test
    public void updateGuestNullCheck() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> guestService.update(null));
    }

    @Test
    public void deleteGuestByExistingId() {
        //given
        createGuest();

        //when and then
        assertTrue(guestService.deleteById(firstGuest.getGuestId()));
    }

    @Test
    public void deleteGuestByIdThrowsExceptionBecauseGuestIdDoesNotExist() {
        //given
        createGuest();
        int invalidId = firstGuest.getGuestId() + 1;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.deleteById(invalidId));
    }

    @Test
    public void deleteExistingGuest() {
        //given
        createGuest();

        //when
        guestService.delete(firstGuest);

        //then
        assertFalse(guestService.findAll().contains(firstGuest));
    }

    @Test
    public void deleteGuestThrowsExceptionBecauseGuestDoesNotExist() {
        //given
        createGuest();
        int invalidId = firstGuest.getGuestId() + 1;
        String firstName = "Mike";
        String lastName = "Miller";
        Gender gender = Gender.MALE;
        Guest guest = new Guest(invalidId, firstName, lastName, gender);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.delete(guest));
    }

    @Test
    public void deleteGuestNullCheck() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> guestService.delete(null));
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
        guestService.saveAll(firstGuest, secondGuest, thirdGuest);
        int expectedSize = 3;

        //when
        List<Guest> guests = guestService.findAll();

        //then
        assertThat(guests, hasSize(expectedSize));
        assertThat(guests, containsInAnyOrder(firstGuest, secondGuest, thirdGuest));
    }

    @Test
    public void createGuestThrowsExceptionBecauseNullOrEmptyFields() {
        //given
        int guestId = 1;
        String emptyFirstName = "";
        String validFirstName = "Pete";
        String lastName = "Miller";
        Gender gender = Gender.MALE;

        //when and then
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(new Guest(guestId, emptyFirstName, lastName, gender)));
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(new Guest(guestId, validFirstName, null, gender)));
    }

    @Test
    public void createGuestNullCheck() {
        //given

        //when and then
        assertThrows(InvalidArgumentException.class, () -> guestService.save(null));
    }

    @Test
    public void createGuestSuccessfully() {
        //given
        int id = 1;
        String firstName = "Maria";
        String lastName = "Johnson";
        Gender gender = Gender.FEMALE;
        firstGuest = new Guest(id, firstName, lastName, gender);

        //when
        guestService.save(firstGuest);

        //then
        assertEquals(firstGuest, guestService.findById(1));
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

    private void createGuest() {
        int id = 1;
        String firstName = "Maria";
        String lastName = "Johnson";
        Gender gender = Gender.FEMALE;

        firstGuest = new Guest(id, firstName, lastName, gender);
        guestService.save(firstGuest);
    }
}