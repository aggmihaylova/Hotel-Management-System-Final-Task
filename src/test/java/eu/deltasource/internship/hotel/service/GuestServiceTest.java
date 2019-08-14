package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GuestServiceTest {
	private GuestService service;
	private Guest guest;

	@BeforeEach
	public void setUp() {
		GuestRepository repo = new GuestRepository();
		service = new GuestService(repo);
		guest = new Guest(1, "Gergana", "Todorova", Gender.FEMALE);
		service.save(guest);
	}

	@Test
	public void findByIdShouldWorkWhenSearchingWithValidId() {
		//Given

		//When
		Guest searchedGuest = service.findById(guest.getGuestId());

		//Then
		assertEquals(guest, searchedGuest);
	}

	@Test
	public void findByIdShouldThrowExceptionIfNoGuestWithSpecifiedId() {
		//Given
		int nonExistingId = guest.getGuestId() + 1;

		//When

		//Then
		assertThrows(ItemNotFoundException.class, () -> service.findById(nonExistingId));
	}

	@Test
	public void updateGuestShouldWorkProperlyIfUpdatingExistingGuest() {
		//Given
		assertEquals("Gergana", guest.getFirstName());
		assertEquals("Todorova", guest.getLastName());
		assertEquals(Gender.FEMALE, guest.getGender());

		//When
		Guest updatedGuest = new Guest(guest.getGuestId(), "Petar", "Ivanov", Gender.MALE);
		assertEquals(updatedGuest, service.updateGuest(updatedGuest));

		//Then
		assertEquals(updatedGuest.getFirstName(), service.findById(guest.getGuestId()).getFirstName());
		assertEquals(updatedGuest.getLastName(), service.findById(guest.getGuestId()).getLastName());
		assertEquals(Gender.MALE, service.findById(guest.getGuestId()).getGender());
	}

	@Test
	public void updateGuestShouldThrowExceptionWhenTryingToUpdateNonExistingOrNullGuest() {
		//Given
		Guest updatedGuest = new Guest(guest.getGuestId() + 1, "Ivan", "Petrov", Gender.MALE);

		//When

		//Then
		assertThrows(ItemNotFoundException.class, () -> service.updateGuest(updatedGuest));
		assertThrows(ArgumentNotValidException.class, () -> service.updateGuest(null));
	}

	@Test
	public void deleteByIdShouldReturnTrueIfGuestWasDeleted() {
		assertTrue(service.deleteById(guest.getGuestId()));
	}

	@Test
	public void deleteByIdShouldThrowExceptionIfGuestWithThisIdNotFound() {
		assertThrows(ItemNotFoundException.class, () -> service.deleteById(guest.getGuestId() + 1));
	}

	@Test
	public void deleteGuestShouldReturnTrueIfGuestExists() {
		//Given
		assertTrue(service.findAll().contains(guest));

		//When
		assertTrue(service.deleteGuest(guest));

		//Then
		assertFalse(service.findAll().contains(guest));
	}

	@Test
	public void deleteGuestShouldReturnFalseIfGuestDoesNotExist() {
		assertThrows(ItemNotFoundException.class,
			() -> service.deleteGuest(new Guest(guest.getGuestId() + 1, "Gergana", "Todorova", Gender.FEMALE)));
	}

	@Test
	public void deleteAllShouldEmptyTheRepositoryList() {
		//Given
		assertFalse(service.findAll().isEmpty());

		//When
		service.deleteAll();

		//Then
		assertTrue(service.findAll().isEmpty());
	}

	@Test
	public void findAllShouldReturnAllGuestsIfAny() {
		//Given
		Guest guest1 = new Guest(2, "Petar", "Petrov", Gender.MALE);
		Guest guest2 = new Guest(3, "Georgi", "Tsankov", Gender.MALE);

		//When
		service.save(guest1);
		service.save(guest2);
		List<Guest> allGuests = service.findAll();

		//Then
		assertThat("The list does not contain the expected number of elements", allGuests, hasSize(3));
		assertThat("The list doesn't contain every element", allGuests, containsInAnyOrder(guest, guest1, guest2));
	}

	@Test
	public void findAllShouldReturnEmptyListIfNoGuests() {
		//Given
		Guest guest1 = new Guest(2, "Petar", "Petrov", Gender.MALE);
		Guest guest2 = new Guest(3, "Georgi", "Tsankov", Gender.MALE);
		service.save(guest1);
		service.save(guest2);
		assertTrue(service.findAll().containsAll(Arrays.asList(guest, guest1, guest2)));

		//When
		service.deleteById(guest.getGuestId());
		service.deleteById(guest1.getGuestId());
		service.deleteById(guest2.getGuestId());

		//Then
		assertTrue(service.findAll().isEmpty());
	}

	@Test
	public void saveShouldThrowExceptionIfNullOrInvalidGuestPassed() {
		assertThrows(ArgumentNotValidException.class, () -> service.save(null));
		assertThrows(FailedInitializationException.class, () -> service.save(new Guest(1, null, null, Gender.MALE)));
	}

	@Test
	public void saveShouldWorkIfProperObjectPassed() {
		//Given
		Guest newGuest = new Guest(2, "Hristo", "Gluhov", Gender.MALE);

		//When

		//Then
		assertDoesNotThrow(() -> service.save(newGuest));
		Guest newGuestFromRepo = service.findById(service.findAll().size());
		assertEquals(newGuest, newGuestFromRepo);
	}

	@Test
	public void saveAllShouldWorkWhenPassedVarargs() {
		//Given
		Guest newGuest = new Guest(2, "Hristo", "Gluhov", Gender.MALE);
		Guest newGuest1 = new Guest(3, "Georgi", "Tsankov", Gender.MALE);

		//When
		service.saveAll(newGuest, newGuest1);
		List<Guest> allGuests = service.findAll();

		//Then
		assertThat("The list does not contain the expected number of elements", allGuests, hasSize(3));
		assertThat("The list doesn't contain every element", allGuests, containsInAnyOrder(guest, newGuest, newGuest1));
	}

	@Test
	public void saveAllShouldWorkWhenGivenProperList() {
		//Given
		Guest newGuest = new Guest(2, "Hristo", "Gluhov", Gender.MALE);
		Guest newGuest1 = new Guest(3, "Georgi", "Tsankov", Gender.MALE);

		//When
		service.saveAll(Arrays.asList(newGuest, newGuest1));
		List<Guest> allGuests = service.findAll();

		//Then
		assertThat("The list does not contain the expected number of elements", allGuests, hasSize(3));
		assertThat("The list doesn't contain every element", allGuests, containsInAnyOrder(guest, newGuest, newGuest1));
	}
}
