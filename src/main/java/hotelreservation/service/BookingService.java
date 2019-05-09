package hotelreservation.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import hotelreservation.repository.ReservationCancellationRepo;
import hotelreservation.repository.ReservationRepo;

@Service
@Transactional(propagation = Propagation.REQUIRED)
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
	private InvoiceService invoiceService;
	
	@Autowired
	private RoomService roomService;
	
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
		
		if(reservation.getCreatedOn() == null) {
			reservation.setCreatedOn(new Date());
		}  else {
			Reservation reservationInDb = reservationRepo.findById(reservation.getId()).orElseThrow(() -> new MissingOrInvalidArgumentException(reservation.getId()));
			reservation.setCreatedOn(reservationInDb.getCreatedOn());
		}
		//TODO checjk if roomRates are present
		
		//Check if room rates have sequential days
		List<RoomRate> roomRates = reservation.getRoomRates();
		if(roomRates == null || roomRates.isEmpty()) {
			throw new MissingOrInvalidArgumentException("Can't have no room rates when updating a reservation");
		}
		
		Map<LocalDate, RoomRate> roomRatesAsMap = new HashMap<LocalDate, RoomRate>();
		
		for (RoomRate roomRate : roomRates) {
			roomRatesAsMap.put(utils.asLocalDate(roomRate.getDay()), roomRate);
		}
		
		LocalDate startDate = utils.asLocalDate(reservation.getStartDate());
		
		long between = ChronoUnit.DAYS.between(startDate,utils.asLocalDate(reservation.getEndDate()));
		
		if(roomRates.size() != between) {
			throw new MissingOrInvalidArgumentException("Not enough rates for the given time frame");
		}
		
		for (int i = 1; i < roomRates.size(); i++) {
			if(!roomRatesAsMap.containsKey(startDate)) {
				throw new MissingOrInvalidArgumentException("Should not be able to save a reservation with non-sequential room rate dates");
			} else {
				startDate = startDate.plusDays(1);
			}
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
		if(!reservationId.isPresent()) {
			log.warn("Missing reservation id.");
			throw new MissingOrInvalidArgumentException("Missing reservation id.");
		}
		
		log.info("Getting resrvation: " + reservationId.get());
		
		Optional<Reservation> reservation = reservationRepo.findById(Long.valueOf(reservationId.get()));
		
		if (!reservation.isPresent()) {
			log.warn("No reservation found for: " + reservationId.get());
			throw new NotFoundException(reservationId.get());
		}
		
		log.info("Got reservation: " + reservation.get().getId());
		
		return reservation.get(); 
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(new Date());
	}

	public void deleteReservation(Reservation reservation) {
		if(!reservationRepo.existsById(reservation.getId())) {
			log.warn("Can't delete reservation that doesn't exist: " + reservation.getId());
			throw new NotDeletedException(reservation.getId());
		}

		log.info("Delete reservation: " + reservation.getId());
		reservationRepo.delete(reservation);
		
		log.info("Deleted reservation: " + reservation.getId());
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

	public void realiseReservation(Reservation reservation) {
		log.info("Realising reservation: " + reservation.getId());
		
		reservation.setReservationStatus(ReservationStatus.InProgress);
		reservationRepo.save(reservation);
		
		log.info("Realised reservation: " + reservation.getId());
	}

	public void fulfillReservation(Optional<Integer> reservationID) {
		Long id = Long.valueOf(reservationID.orElseThrow(() -> new  MissingOrInvalidArgumentException("Reservation fulfillment ID is missing")));
		
		if(!reservationRepo.existsById(id)) {
			throw new NotFoundException("Reservation fulfillment reservation is missing. ID " + id);
		}
		
		Reservation reservation = reservationRepo.findById(id).get();
		
		if(!reservation.getReservationStatus().equals(ReservationStatus.InProgress)) {
			throw new MissingOrInvalidArgumentException("Reservation in wrong state for fulfillment. Was: " + reservation.getReservationStatus());
		}
		
		if(!invoiceService.areAllChargesPaidFor(reservation)) {
			throw new MissingOrInvalidArgumentException("Not all reservation charges have been paid for." + reservation.getReservationStatus());
		}
		
		reservation.setReservationStatus(ReservationStatus.Fulfilled);
		reservationRepo.save(reservation);
		
		log.info("Fulfilled reservation: " + reservation.getId());
	}

	public long getReservationCount() {
		return reservationRepo.count();
	}

	public void saveReservationAndValidateRoomRates(Reservation reservation, List<Long> roomRateIds) {
		if(roomRateIds == null || roomRateIds.isEmpty()) {
			throw new MissingOrInvalidArgumentException("No RoomRates were selected for reservation: " + reservation.getId());
		}
		
		List<RoomRate> roomRates = new ArrayList<>();
		
		roomRateIds.stream().forEach(id -> roomRates.add(roomService.getRoomRateById(id)));
		reservation.setRoomRates(roomRates);
		
		saveReservation(reservation);
		System.err.println(roomRates.size());
	}
}