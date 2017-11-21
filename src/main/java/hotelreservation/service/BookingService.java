package hotelreservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import hotelreservation.repository.ReservationRepo;

@Service
public class BookingService {
	
	@Autowired
	private GuestRepo guestRepo;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private IdentificationRepo identificationRepo;
	
	@Autowired
	private ReservationRepo reservationRepo;
	
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

	public void createReservation(Reservation reservation) {
		reservationRepo.save(reservation);
	}

}
