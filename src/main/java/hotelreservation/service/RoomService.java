package hotelreservation.service;

import java.util.ArrayList;
import java.util.Arrays;
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

import hotelreservation.NotFoundException;
import hotelreservation.Utils;
import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.repository.AmenityRepo;
import hotelreservation.repository.AmenityTypeRepo;
import hotelreservation.repository.ReservationRepo;
import hotelreservation.repository.RoomRateRepo;
import hotelreservation.repository.RoomRepo;
import hotelreservation.repository.RoomTypeRepo;
import hotelreservation.repository.StatusRepo;

@Service
@Transactional
public class RoomService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomRepo roomRepo;

	@Autowired
	private RoomTypeRepo roomTypeRepo;

	@Autowired
	private StatusRepo statusRepo;

	@Autowired
	private AmenityTypeRepo amenityTypeRepo;

	@Autowired
	private AmenityRepo amenityRepo;

	@Autowired
	private RoomRateRepo roomRateRepo;
	
	@Autowired
	private ReservationRepo reservationRepo;
	
	@Autowired
	private Utils utils;

	public List<Amenity> getRoomAmenities() {
		List<Amenity> roomAmenities = new ArrayList<>();

		for (AmenityType amenityType : amenityTypeRepo.findAll()) {
			if (!amenityType.getName().equals("Hotel")) {
				roomAmenities.addAll(amenityRepo.findByAmenityType(amenityType));
			}
		}

		return roomAmenities;
	}

	public List<Room> getByRoomsByStatus(Status status) {
		List<Room> target = new ArrayList<Room>();
		roomRepo.findByStatus(status).forEach(target::add);
		return target;
	}

	public List<RoomRate> getRoomRates(Date start, Date end) {
		List<RoomRate> findByStartDateBetween = roomRateRepo.findByDayBetween(start, end);
		return findByStartDateBetween;
	}
	
	public List<RoomRate> getRoomRates(Room room, Date start, Date end) {
		List<RoomRate> findByStartDateBetween = roomRateRepo.findByRoomIdAndDayBetween(room.getId(), start, end);
		return findByStartDateBetween;
	}
	
	public List<RoomRate> getAvailableRoomRates(Date start, Date end) { //TODO add a variant of this method but for a particular room 
		List<RoomRate> availableRoomRates = new ArrayList<RoomRate>();
		List<Reservation> inProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		List<RoomRate> availableRoomRatesForAllRooms = getRoomRates(start, end);

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

	public Map<Room, List<RoomRate>> getRoomRatesAsMap(Date startDate, Date endDate) {
		Map<Room, List<RoomRate>> ratesForAllRooms = new HashMap<Room, List<RoomRate>>();
		
		for (RoomRate roomRate : getAvailableRoomRates(startDate, endDate)) {
			
			if(ratesForAllRooms.containsKey(roomRate.getRoom())) {
				ratesForAllRooms.get(roomRate.getRoom()).add(roomRate);
			} else {
				ratesForAllRooms.put(roomRate.getRoom(), new ArrayList<RoomRate>(Arrays.asList(roomRate)));
			}
		}

		return ratesForAllRooms;
	}

	//---- Amenity
	public Amenity createAmenity(Amenity amenity) {
		return amenityRepo.save(amenity);
	}
	
	public Amenity getAmenityById(long id) {
		log.info("Looking for Amenity with ID: " + id);
		Optional<Amenity> findById = amenityRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public List<Amenity> getAllAmenities() {
		List<Amenity> target = new ArrayList<Amenity>();
		amenityRepo.findAll().forEach(target::add);
		return target;
	}
	
	public void deleteAmenity(Long id) {
		amenityRepo.deleteById(id);
	}
	
	//---- AmenityType
	public AmenityType createAmenityType(AmenityType amenityType) {
		return amenityTypeRepo.save(amenityType);
	}
	
	public AmenityType getAmenityTypeById(long id) {
		log.info("Looking for AmenityType with ID: " + id);
		Optional<AmenityType> findById = amenityTypeRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public List<AmenityType> getAllAmenityTypes() {
		return utils.toList(amenityTypeRepo.findAll());
	}
	
	public void deleteAmenityType(Long id) {
		amenityTypeRepo.deleteById(id);
	}
	
	//---- Room
	public Room createRoom(Room room) {
		room.setCreatedOn(new Date());
		return roomRepo.save(room);
	}
	
	public Room getRoomById(long id) {
		log.info("Looking for Room with ID: " + id);
		Optional<Room> findById = roomRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public List<Room> getAllRooms() {
		return utils.toList(roomRepo.findAll());
	}
	
	public void deleteRoom(Long id) {
		roomRepo.deleteById(id);
	}
	
	public void deleteRoom(Room room) {
		roomRepo.delete(room);
	}
	
	//---- RoomType
	public RoomType createRoomType(RoomType roomType) {
		return roomTypeRepo.save(roomType);
	}
	
	public RoomType getRoomTypeById(long id) {
		log.info("Looking for RoomType with ID: " + id);
		Optional<RoomType> findById = roomTypeRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public List<RoomType> getAllRoomTypes() {
		return utils.toList(roomTypeRepo.findAll());
	}
	
	public void deleteRoomType(Long id) {
		roomTypeRepo.deleteById(id);
	}
	
	public void deleteRoomType(RoomType roomType) {
		roomTypeRepo.delete(roomType);
	}
	
	//---- Room Rate
	public void createRoomRate(RoomRate roomRate) {
		roomRateRepo.save(roomRate);
	}

	public RoomRate getRoomRateById(long id) {
		log.info("Looking for RoomRate with ID: " + id);
		Optional<RoomRate> findById = roomRateRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}

	public List<RoomRate> getAllRoomRates() { 
		return utils.toList(roomRateRepo.findAll());
	}
	
	public void deleteRoomRate(Long id) {
		roomRateRepo.deleteById(id);
	}
	
	//---- Status
	public Status createStatus(Status status) {
		return statusRepo.save(status);
	}
	
	public Status getStatusById(long id) {
		log.info("Looking for Status with ID: " + id);
		Optional<Status> findById = statusRepo.findById(id);
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}

	public List<Status> getAllStatuses() {
		return utils.toList(statusRepo.findAll());
	}
	
	public void deleteStatus(Long id) {
		statusRepo.deleteById(id);
	}
}