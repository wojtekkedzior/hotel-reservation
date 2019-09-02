package hotelreservation.service;

import java.util.List;
import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hotelreservation.Utils;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;

@Service
@Transactional
public class GuestService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private GuestRepo guestRepo;
	
	@Autowired
	private IdentificationRepo identificationRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private Utils utils;
	
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
		log.info("Looking for Guest with ID: {}", id);
		return guestRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public Contact getContactById(long id) {
		log.info("Looking for Contact with ID: {}", id);
		return contactRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public Identification getIdentificationById(long id) {
		log.info("Looking for Identification with ID: {}", id);
		return identificationRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public List<Contact> getAllContacts() {
		return utils.toList(contactRepo.findAll());
	}
	
	public List<Guest> getAllGuests() {
		return utils.toList(guestRepo.findAll());
	}
	
	public List<Identification> getAllIdentifications() {
		return utils.toList(identificationRepo.findAll());
	}
	
	public void deleteGuest(Optional<Integer> id) {
		if(!guestRepo.existsById(Long.valueOf(id.get()))) {
			throw new NotDeletedException(id.get());
		}
		guestRepo.delete(guestRepo.findById(Long.valueOf(id.get())).get());
	}
	
	public void deleteIdentification(Optional<Integer> id) {
		if(!identificationRepo.existsById(Long.valueOf(id.get()))) {
			throw new NotDeletedException(id.get());
		}
		identificationRepo.delete(identificationRepo.findById(Long.valueOf(id.get())).get());
	}

	public void deleteContact(long id) {
		if(!contactRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		contactRepo.deleteById(id);
	}
}