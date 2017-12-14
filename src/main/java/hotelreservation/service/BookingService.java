package hotelreservation.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		if (reservation.getMainGuest().getId() == 0) {
			guestRepo.save(reservation.getMainGuest());
		}

		reservationRepo.save(reservation);
	}

	public List<Reservation> getAllReservations() {
		List<Reservation> target = new ArrayList<Reservation>();
		reservationRepo.findAll().forEach(target::add);
		return target;
	}

	public Reservation getReservation(int reservationId) {
		return reservationRepo.findById(new Long(reservationId)).get();
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(new Date());
	}
}
