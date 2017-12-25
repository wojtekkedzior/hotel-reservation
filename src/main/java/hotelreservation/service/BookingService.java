package hotelreservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.DateConvertor;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.RoomRate;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import hotelreservation.repository.ReservationRepo;
import hotelreservation.repository.RoomRateRepo;

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
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private DateConvertor dateConvertor;
	
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
		
		//validate reservation has start and end date
		
		
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
		
		reservationRepo.save(reservation);
	}

	public List<RoomRate> getAvailableRoomRates(Date start, Date end) { //TODO add a variant of this method but for a particular room and move thos to roomservice
		List<RoomRate> availableRoomRates = new ArrayList<RoomRate>();
		List<Reservation> inProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		List<RoomRate> availableRoomRatesForAllRooms = roomService.getRoomRatesForAllRooms(start, end);

		for (RoomRate roomRate : availableRoomRatesForAllRooms) {
			//No reservations so rate is available
			if(inProgressAndUpComingReservations.isEmpty()) {
				availableRoomRates.add(roomRate);
				continue;
			}
			
			boolean isRoomRatesAvailable = false;
			
			for (Reservation reservation : inProgressAndUpComingReservations) {
				if(!reservation.getRoomRates().contains(roomRate)) {
					//Rate is available for this reservation, but need to check all the others,
					isRoomRatesAvailable = true;
				} else {
					//One reservation has this rate already, therefore it's no longer available
					isRoomRatesAvailable = false;
					break;
				}
			}
			
			if(isRoomRatesAvailable) {
				availableRoomRates.add(roomRate);
				isRoomRatesAvailable=false;
			}
		}
		
		return availableRoomRates;
	}
	
	public List<Reservation> getAllReservations() {
		List<Reservation> target = new ArrayList<Reservation>();
		reservationRepo.findAll().forEach(target::add);
		return target;
	}

	public Reservation getReservation(int reservationId) {
		return reservationRepo.findOne(new Long(reservationId));
	}
 
	public List<Reservation> getReservationsStartingToday() {
		return reservationRepo.findByStartDate(new Date());
	}
}
