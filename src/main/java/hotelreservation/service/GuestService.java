package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;

@Service
public class GuestService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private GuestRepo guestRepo;
	
	@Autowired
	private IdentificationRepo identificationRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	public Contact saveContact(Contact contact) {
		return contactRepo.save(contact);
	}
	
	public Guest saveGuest(Guest guest) {
		return guestRepo.save(guest);
	}
	
	public Identification saveIdentification(Identification identification) {
		return identificationRepo.save(identification);
	}

	public Guest getGuestById(long id) {
		log.info("Looking for Guest with ID: " + id);
		if(guestRepo.findById(id).isPresent()) {
			return guestRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public Contact getContactById(long id) {
		log.info("Looking for Contact with ID: " + id);
		if(contactRepo.findById(id).isPresent()) {
			return contactRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public Identification getIdentificationById(long id) {
		log.info("Looking for Identification with ID: " + id);
		if(identificationRepo.findById(id).isPresent()) {
			return identificationRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public List<Contact> getAllContacts() {
		List<Contact> target = new ArrayList<Contact>();
		contactRepo.findAll().forEach(target::add);
		return target;
	}
	
	public List<Guest> getAllGuests() {
		List<Guest> target = new ArrayList<Guest>();
		guestRepo.findAll().forEach(target::add);
		return target;
	}
	
	public List<Identification> getAllIdentifications() {
		List<Identification> target = new ArrayList<Identification>();
		identificationRepo.findAll().forEach(target::add);
		return target;
	}
	
	public void deleteGuest(Optional<Integer> id) {
		guestRepo.delete(guestRepo.findById(new Long(id.get())).get());
	}
	
	public void deleteContact(Optional<Integer> id) {
		contactRepo.delete(contactRepo.findById(new Long(id.get())).get());
	}
	
	public void deleteIdentification(Optional<Integer> id) {
		identificationRepo.delete(identificationRepo.findById(new Long(id.get())).get());
	}

	public void deleteContact(long id) {
		contactRepo.deleteById(id);
	}
}