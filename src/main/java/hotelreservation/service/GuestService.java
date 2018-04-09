package hotelreservation.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public Guest findGuest(long id) {
		return guestRepo.findOne(id);
	}
	
	public Contact findContact(long id) {
		return contactRepo.findOne(id);
	}
	
	public Identification findIdentification(long id) {
		return identificationRepo.findOne(id);
	}
	
	public void deleteGuest(Optional<Integer> id) {
		guestRepo.delete(guestRepo.findOne(new Long(id.get())));
	}
	
	public void deleteContact(Optional<Integer> id) {
		contactRepo.delete(contactRepo.findOne(new Long(id.get())));
	}
	
	public void deleteIdentification(Optional<Integer> id) {
		identificationRepo.delete(identificationRepo.findOne(new Long(id.get())));
	}

	public void deleteContact(long id) {
		contactRepo.delete(id);
		
	}


	
}
