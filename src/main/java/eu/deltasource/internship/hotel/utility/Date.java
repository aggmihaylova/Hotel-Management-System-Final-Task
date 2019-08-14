package eu.deltasource.internship.hotel.utility;


import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Date {

	private LocalDate from;
	private LocalDate to;

	public Date(LocalDate from, LocalDate to) {
		setFrom(from);
		setTo(to);
	}

	public void setFrom(LocalDate from) {
		if (from == null) {
			throw new FailedInitializationException("Invalid date !");
		}
		this.from = from;
	}

	public void setTo(LocalDate to) {
		if (to == null) {
			throw new FailedInitializationException("Invalid date !");
		}
		this.to = to;
	}
}
