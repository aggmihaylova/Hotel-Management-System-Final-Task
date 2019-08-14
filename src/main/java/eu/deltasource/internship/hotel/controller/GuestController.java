package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

	@Autowired
	private GuestService guestService;

	@PostMapping(value = "/list")
	public List<Guest> saveAll(@RequestBody List<Guest> guests) {
		return guestService.saveAll(guests);
	}

	@PostMapping
	public Guest save(@RequestBody Guest guest) {
		return guestService.save(guest);
	}

	@GetMapping(value = "/{id}")
	public Guest findById(@PathVariable("id") int id) {
		return guestService.findById(id);
	}

	@GetMapping
	public List<Guest> findAll() {
		return guestService.findAll();
	}

	@PutMapping
	public Guest updateGuest(@RequestBody Guest guest) {
		return guestService.updateGuest(guest);
	}

	@DeleteMapping(value = "/{id}")
	public boolean deleteById(@PathVariable("id") int id) {
		return guestService.deleteById(id);
	}

	@DeleteMapping(value = "/all")
	public void deleteAll() {
		guestService.deleteAll();
	}

	@DeleteMapping
	public boolean deleteGuest(@RequestBody Guest guest) {
		return guestService.deleteGuest(guest);
	}
}
