package hotelreservation.service;

import java.util.Optional;

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
	
	@Autowired
	private GuestRepo guestRepo;
	
	@Autowired
	private IdentificationRepo identificationRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	public Contact createContact(Contact contact) {
		return contactRepo.save(contact);
	}
	
	public Guest createGuest(Guest guest) {
		return guestRepo.save(guest);
	}
	
	public Identification createIdentification(Identification identification) {
		return identificationRepo.save(identification);
	}

	public void deleteGuest(Optional<Integer> id) {
		guestRepo.delete(guestRepo.findOne(new Long(id.get())));
	}
	
}
