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
public class BookingService {
	
	@Autowired
	private GuestRepo guestRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private IdentificationRepo identificationRepo;
	
	
	public void getRoomStatus(long roomId) {
		
	}

	public void createContact(Contact contact) {
		contactRepo.save(contact);
	}

	public void createGuest(Guest guest) {
		guestRepo.save(guest);
	}

	public void createIdentification(Identification identification) {
		identificationRepo.save(identification);
	}

}
