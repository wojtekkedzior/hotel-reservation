package hotelreservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BookingService {
	private final GuestRepo guestRepo;
	private final ContactRepo contactRepo;
	private final IdentificationRepo identificationRepo;
	private final ReservationRepo reservationRepo;
	private final ReservationCancellationRepo reservationCancellationRepo;
	private final Utils utils;
	private final InvoiceService invoiceService;

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
		if(reservation.getStartDate() == null || reservation.getEndDate() == null || reservation.getStartDate().isAfter(reservation.getEndDate())) {
			throw new MissingOrInvalidArgumentException("Start and/or end dates cannot be empty");
		}
		
		if(reservation.getCreatedOn() == null) {
			reservation.setCreatedOn(LocalDateTime.now());
		}  else {
			Reservation reservationInDb = reservationRepo.findById(reservation.getId()).orElseThrow(() -> new MissingOrInvalidArgumentException("Missing reservation id: " + reservation.getId()));
			reservation.setCreatedOn(reservationInDb.getCreatedOn());
		}

		List<RoomRate> roomRates = reservation.getRoomRates();

		if(roomRates == null || roomRates.isEmpty()) {
			throw new MissingOrInvalidArgumentException("Can't have no room rates when updating a reservation");
		} else if(roomRates.size() != ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate())) {
			throw new MissingOrInvalidArgumentException("Not enough rates for the given time frame. Found: " + roomRates.size() + " expected: " + ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate()));
		}

		Map<LocalDate, RoomRate> roomRatesAsMap = roomRates.stream().collect(Collectors.toMap(RoomRate::getDay, Function.identity()));

		for (int i = 0; i < roomRates.size(); i++) {
			if (!roomRatesAsMap.containsKey(reservation.getStartDate().plus(1, ChronoUnit.DAYS))) {
				throw new MissingOrInvalidArgumentException("Should not be able to save a reservation with non-sequential room rate dates");
			}
		}

		//Check if roomRates are available
		List<Reservation> findInProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		removeReservationIfPresent(reservation, findInProgressAndUpComingReservations);

		if (roomRates.stream()
				.filter(roomRate -> findInProgressAndUpComingReservations.stream()
						.map(Reservation::getRoomRates)
						.flatMap(List::stream)
						.collect(Collectors.toList())
						.contains(roomRate))
				.count() > 0 ) {
			throw new MissingOrInvalidArgumentException("No rooms available for the given day");
		}

		reservation.setReservationStatus(ReservationStatus.UP_COMING);
		
		reservationRepo.save(reservation);
	}

	private void removeReservationIfPresent(Reservation reservation, List<Reservation> findInProgressAndUpComingReservations) {
		Comparator<Reservation> comparator = Comparator.comparingLong(Reservation::getId);
		boolean contains = utils.contains(findInProgressAndUpComingReservations, reservation, comparator);
		
		if(contains) {
			findInProgressAndUpComingReservations.remove(reservation);
		}
	}
	
	List<Reservation> getAllReservations() {
		return utils.toList(reservationRepo.findAll());
	}

	public Reservation getReservation(Long reservationId) {
		if(reservationId == null) {
			throw new MissingOrInvalidArgumentException("ReservationID cannot be null");
		}

		log.info("Getting reservation: {}", reservationId);
		Reservation reservation = reservationRepo.findById(reservationId).orElseThrow(() -> new MissingOrInvalidArgumentException("missing reservation for id: {} " + reservationId));
		log.info("Got reservation: {}", reservation.getId());

		return reservation;
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(LocalDate.now());
	}

	public void deleteReservation(Reservation reservation) {
		if(!reservationRepo.existsById(reservation.getId())) {
			log.warn("Can't delete reservation that doesn't exist: {}", reservation.getId());
			throw new NotDeletedException(reservation.getId());
		}

		log.info("Delete reservation: {}", reservation.getId());
		reservationRepo.delete(reservation);
		log.info("Deleted reservation: {}", reservation.getId());
	}

	public List<Reservation> getReservationsByStatus(ReservationStatus reservationStatus) {
		return reservationRepo.findByReservationStatus(reservationStatus);
	}
	
	public void cancelReservation(ReservationCancellation reservationCancellation) {
		log.info("Cancelling reservation: {}", reservationCancellation.getReservation().getId());
		
		reservationCancellationRepo.save(reservationCancellation);
		
		switch (reservationCancellation.getReservation().getReservationStatus()) {
		case UP_COMING:
			reservationCancellation.getReservation().setReservationStatus(ReservationStatus.CANCELLED);
			break;
		case IN_PROGRESS:
			reservationCancellation.getReservation().setReservationStatus(ReservationStatus.ABANDONED);
			break;
		default:
			throw new MissingOrInvalidArgumentException("Cannot cancel a reservation: " + reservationCancellation.getReservation().getId() + 
					" because it's in status of: " + reservationCancellation.getReservation().getReservationStatus());
		}

		List<RoomRate> roomRatesUsed = reservationCancellation.getReservation().getRoomRates().stream()
				.filter(roomRate -> roomRate.getDay().isBefore(LocalDate.now()) || roomRate.getDay().isEqual(LocalDate.now()))
				.collect(Collectors.toList());

		reservationCancellation.getReservation().setRoomRates(roomRatesUsed);
		reservationRepo.save(reservationCancellation.getReservation());
		
		log.info("Cancelled reservation: {}", reservationCancellation.getReservation().getId());
	}

	public void realiseReservation(Reservation reservation) {
		log.info("Realising reservation: {}", reservation.getId());
		
		reservation.setReservationStatus(ReservationStatus.IN_PROGRESS);
		reservationRepo.save(reservation);
		
		log.info("Realised reservation: {}", reservation.getId());
	}

	public void fulfillReservation(Long reservationID) {
		if(reservationID == null) {
			throw new MissingOrInvalidArgumentException("Empty ID");
		}
		
		Reservation reservation = reservationRepo.findById(reservationID).orElseThrow( () -> new MissingOrInvalidArgumentException("Missing reservation for id: {}" + reservationID));

		if(!reservation.getReservationStatus().equals(ReservationStatus.IN_PROGRESS)) {
			throw new MissingOrInvalidArgumentException("Reservation in wrong state for fulfillment. Was: " + reservation.getReservationStatus());
		}
		
		if(!invoiceService.areAllChargesPaidFor(reservation)) {
			throw new MissingOrInvalidArgumentException("Not all reservation charges have been paid for." + reservation.getReservationStatus());
		}
		
		reservation.setReservationStatus(ReservationStatus.FULFILLED);
		reservationRepo.save(reservation);
		
		log.info("Fulfilled reservation: {}", reservation.getId());
	}

	public long getReservationCount() {
		return reservationRepo.count();
	}
}

