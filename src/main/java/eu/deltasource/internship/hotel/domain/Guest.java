package eu.deltasource.internship.hotel.domain;

import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import lombok.*;


/**
 * Represents information for a hotel guest
 */
@Getter
public class Guest {

	private final int guestId;
	private Gender gender;
	private String firstName;
	private String lastName;

	public Guest(int guestId, String firstName, String lastName, Gender gender) {
		this.guestId = guestId;
		this.gender = gender;
		initializeNamesAndNullChecks(firstName, lastName);
	}

	/**
	 * This constructor should be used
	 * only by the repository.
	 */
	public Guest(Guest guest) {
		guestId = guest.guestId;
		gender = guest.gender;
		initializeNamesAndNullChecks(guest.firstName, guest.lastName);
	}

	private void initializeNamesAndNullChecks(String firstName, String lastName) {
		if (firstName == null || lastName == null ||
			firstName.isEmpty() || lastName.isEmpty()) {
			throw new FailedInitializationException("Guest name is invalid");
		} else if (gender == null) {
			throw new FailedInitializationException("Gender is not set");
		}
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Guest)) {
			return false;
		}
		return guestId == ((Guest) obj).guestId;
	}

	@Override
	public int hashCode() {
		return guestId;
	}

	public void changeFirstAndLastNames(String firstName, String lastName) {
		initializeNamesAndNullChecks(firstName, lastName);
	}

	public void changeGender(Gender gender) {
		this.gender = gender;
	}
}
