package hotelreservation.service;

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
	
	
	
}
