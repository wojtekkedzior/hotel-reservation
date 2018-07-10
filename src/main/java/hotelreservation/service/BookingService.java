package hotelreservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.DateConvertor;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.ReservationCheckout;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import hotelreservation.repository.ReservationCancellationRepo;
import hotelreservation.repository.ReservationCheckoutRepo;
import hotelreservation.repository.ReservationRepo;

@Service
@Transactional
public class BookingService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private GuestRepo guestRepo;

	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private IdentificationRepo identificationRepo;

	@Autowired
	private ReservationRepo reservationRepo;
	
	@Autowired
	private ReservationCancellationRepo reservationCancellationRepo;
	
	@Autowired
	private DateConvertor dateConvertor;

	@Autowired
	private ReservationCheckoutRepo reservationCheckoutRepo;
	
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
		if(guestRepo.existsById(reservation.getId())) {
			reservationRepo.save(reservation);
			//TODO set modified dates etc here too
			return;
		}
		
		if (reservation.getMainGuest().getId() == 0) {
			guestRepo.save(reservation.getMainGuest());
		}
		
		//TODO validate reservation has start and end date
		
		//TODO createReservation should fail if there are not available rooms.
		
		//Check if room rates have sequential days
		List<RoomRate> roomRates = reservation.getRoomRates();
		
		Map<LocalDate, RoomRate> roomRatesAsMap = new HashMap<LocalDate, RoomRate>();
		
		for (RoomRate roomRate : roomRates) {
			roomRatesAsMap.put(dateConvertor.asLocalDate(roomRate.getDay()), roomRate);
		}
		
		LocalDate startDate = dateConvertor.asLocalDate(reservation.getStartDate());
		
		for (int i = 0; i < roomRates.size(); i++) {
			if(!roomRatesAsMap.containsKey(startDate)) {
				throw new IllegalArgumentException("Should not be able to save a reservation with non-sequential room rate dates");
			} else {
				startDate = startDate.plusDays(1);
			}
		}
		
		//Check if roomRates are available
		List<Reservation> findInProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		for (Reservation reservation2 : findInProgressAndUpComingReservations) {
			for (RoomRate roomRate : roomRates) {
				if(reservation2.getRoomRates().contains(roomRate)) {
					throw new IllegalArgumentException("No rooms available for the given day");
				} 
			}
		}
		
		reservation.setCreatedOn(new Date());
		reservation.setReservationStatus(ReservationStatus.UpComing);
		reservationRepo.save(reservation);
	}

	public List<Reservation> getAllReservations() {
		List<Reservation> target = new ArrayList<Reservation>();
		reservationRepo.findAll().forEach(target::add);
		return target;
	}

	public Reservation getReservation(Optional<Integer> reservationId) {
		return reservationRepo.findById(new Long(reservationId.get())).get();
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(new Date());
	}

	public Reservation getReservation(long id) {
		return reservationRepo.findById(id).get();
	}

	public void deleteReservation(Reservation reservation) {
		reservationRepo.delete(reservation);
	}

	public void saveReservation(Reservation reservationOne) {
		reservationRepo.save(reservationOne);
	}

	public List<Reservation> getReservationsByStatus(ReservationStatus reservationStatus) {
		return reservationRepo.findByReservationStatus(reservationStatus);
	}
	
	public void cancelReservation(ReservationCancellation reservationCancellation) {
		log.info("Cancelling reservation: " + reservationCancellation.getReservation().getId());
		reservationCancellationRepo.save(reservationCancellation);
		
		switch (reservationCancellation.getReservation().getReservationStatus()) {
		case UpComing:
			reservationCancellation.getReservation().setReservationStatus(ReservationStatus.Cancelled);
			break;
		case InProgress:
			reservationCancellation.getReservation().setReservationStatus(ReservationStatus.Abandoned);
			break;
		default:
			break;
		}
		
		reservationCancellation.getReservation().setRoomRates(null);
		reservationRepo.save(reservationCancellation.getReservation());
		
		log.info("Cancelled reservation: " + reservationCancellation.getReservation().getId());
	}

	public void checkoutReservation(Reservation resFromDB, ReservationCheckout reservationCheckout) {
		resFromDB.setReservationStatus(ReservationStatus.Fulfilled);
		reservationRepo.save(resFromDB);
		reservationCheckoutRepo.save(reservationCheckout);
	}
}
