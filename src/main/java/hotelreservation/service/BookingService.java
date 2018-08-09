package hotelreservation.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
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
	private Utils utils;

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

	public void saveReservation(Reservation reservation) {
		if(reservation.getStartDate() == null || reservation.getEndDate() == null) {
			throw new MissingOrInvalidArgumentException("Start and/or end dates cannot be empty");
		} else if(reservation.getStartDate().after(reservation.getEndDate())) {
			throw new MissingOrInvalidArgumentException("Start and/or end dates cannot be empty");
		}
		
		if (reservation.getMainGuest().getId() == 0) {
			guestRepo.save(reservation.getMainGuest());
		}
		
		if(reservation.getCreatedOn() == null) {
			reservation.setCreatedOn(new Date());
		}  else {
			Optional<Reservation> findById = reservationRepo.findById(reservation.getId());
			
			if(!findById.isPresent()) {
				throw new MissingOrInvalidArgumentException(reservation.getId());
			} else {
				reservation.setCreatedOn(findById.get().getCreatedOn());
			}
		}
		
		//Check if room rates have sequential days
		List<RoomRate> roomRates = reservation.getRoomRates();
		
		Map<LocalDate, RoomRate> roomRatesAsMap = new HashMap<LocalDate, RoomRate>();
		
		for (RoomRate roomRate : roomRates) {
			roomRatesAsMap.put(utils.asLocalDate(roomRate.getDay()), roomRate);
		}
		
		LocalDate startDate = utils.asLocalDate(reservation.getStartDate());
		
		long between = ChronoUnit.DAYS.between(startDate,utils.asLocalDate(reservation.getEndDate()));
		between += 1;
		
		if(roomRates.size() != between) {
			throw new MissingOrInvalidArgumentException("Not enough rates for the given time frame");
		}
		
		for (int i = 0; i < roomRates.size(); i++) {
			if(!roomRatesAsMap.containsKey(startDate)) {
				throw new MissingOrInvalidArgumentException("Should not be able to save a reservation with non-sequential room rate dates");
			} else {
				startDate = startDate.plusDays(1);
			}
		}
		
		if(reservation.getRoomRates() == null || reservation.getRoomRates().isEmpty()) {
			throw new MissingOrInvalidArgumentException("Can't have no room rates when updating a reservation");
		}
		
		//Check if roomRates are available
		List<Reservation> findInProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		removeReservationIfPresent(reservation, findInProgressAndUpComingReservations);
		
		for (Reservation reservation2 : findInProgressAndUpComingReservations) {
			for (RoomRate roomRate : roomRates) {
				if(reservation2.getRoomRates().contains(roomRate)) {
					throw new MissingOrInvalidArgumentException("No rooms available for the given day");
				} 
			}
		}
		
		reservation.setReservationStatus(ReservationStatus.UpComing);
		
		reservationRepo.save(reservation);
	}

	private void removeReservationIfPresent(Reservation reservation, List<Reservation> findInProgressAndUpComingReservations) {
		Comparator<Reservation> comparator = new Comparator<Reservation>() {
		    @Override
		    public int compare(Reservation s1, Reservation s2) {
		        return Long.compare(s1.getId(), s2.getId());
		    }
		};
		
		boolean contains = utils.contains(findInProgressAndUpComingReservations, reservation, comparator);
		
		if(contains) {
			findInProgressAndUpComingReservations.remove(reservation);
		}
	}
	
	public List<Reservation> getAllReservations() {
		return utils.toList(reservationRepo.findAll());
	}

	public Reservation getReservation(Optional<Integer> reservationId) {
		Optional<Reservation> reservation = reservationRepo.findById(new Long(reservationId.get()));
		
		if (!reservation.isPresent()) {
			throw new NotFoundException(reservationId.get());
		}
		
		return reservation.get(); 
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(new Date());
	}

	public void deleteReservation(Reservation reservation) {
		if(!reservationRepo.existsById(reservation.getId())) {
			throw new NotDeletedException(reservation.getId());
		}
		reservationRepo.delete(reservation);
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
		if(resFromDB.getReservationStatus().equals(ReservationStatus.Fulfilled)) {
			throw new MissingOrInvalidArgumentException("Reservation already fulfiled. ID: " + resFromDB.getId());
		}
		
		if(reservationCheckout.getCheckedout() == null) {
			throw new MissingOrInvalidArgumentException("Missing reservation checkout date. ID: " + resFromDB.getId());
		}
		
		if(reservationCheckout.getPayment() == null) {
			throw new MissingOrInvalidArgumentException("Missing payments. ID: " + resFromDB.getId());
		}
		
		resFromDB.setReservationStatus(ReservationStatus.Fulfilled);
		reservationRepo.save(resFromDB);
		reservationCheckoutRepo.save(reservationCheckout);
	}

	public void realiseReservation(Reservation reservation) {
		reservation.setReservationStatus(ReservationStatus.InProgress);
		reservationRepo.save(reservation);
	}
}