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
		if(guestRepo.findById(id).isPresent()) {
			return guestRepo.findById(id).get();
		} else {
			return null; //TODO
		}
	}
	
	public Contact findContact(long id) {
		if(contactRepo.findById(id).isPresent()) {
			return contactRepo.findById(id).get();
		} else {
			return null; //TODO
		}
	}
	
	public Identification findIdentification(long id) {
		if(identificationRepo.findById(id).isPresent()) {
			return identificationRepo.findById(id).get();
		} else {
			return null;
		}
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
