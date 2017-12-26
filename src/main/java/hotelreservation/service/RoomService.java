package hotelreservation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class RoomService {

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

	public RoomType createRoomType(RoomType roomType) {
		return roomTypeRepo.save(roomType);
	}

	public List<Room> getAllRooms() {
		List<Room> target = new ArrayList<Room>();
		roomRepo.findAll().forEach(target::add);

		return target;
	}

	public Status createStatus(Status status) {
		return statusRepo.save(status);
	}

	public List<Status> getAllStatuses() {
		List<Status> target = new ArrayList<Status>();
		statusRepo.findAll().forEach(target::add);

		return target;
	}

	public List<RoomType> getAllRoomTypes() {
		List<RoomType> target = new ArrayList<RoomType>();
		roomTypeRepo.findAll().forEach(target::add);

		return target;
	}

	public RoomType getRoomTypeById(long id) {
		return roomTypeRepo.findOne(id);
	}

	public void deleteRoomType(RoomType roomType) {
		roomTypeRepo.delete(roomType);
	}

	public Room createRoom(Room room) {
		room.setCreatedOn(new Date());
		return roomRepo.save(room);
	}

	public Room getRoomById(long id) {
		return roomRepo.findOne(id);
	}

	public AmenityType createAmenityType(AmenityType amenityType) {
		return amenityTypeRepo.save(amenityType);
	}

	public Amenity createAmenity(Amenity ammenity) {
		return amenityRepo.save(ammenity);
	}

	public List<AmenityType> getAllAmenityTypes() {
		List<AmenityType> target = new ArrayList<AmenityType>();
		amenityTypeRepo.findAll().forEach(target::add);

		return target;
	}

	public List<Amenity> getAllAmenities() {
		List<Amenity> target = new ArrayList<Amenity>();
		amenityRepo.findAll().forEach(target::add);

		return target;
	}

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

	public Status getRoomStatus(Room room) {
		return roomRepo.findOne(room.getId()).getStatus();
	}

	public void createRoomRate(RoomRate roomRate) {
		roomRateRepo.save(roomRate);
	}

	public List<RoomRate> getRoomRatesForAllRooms(Date start, Date end) {
		System.err.println("start: " + start);
		System.err.println("start: " + end);
		List<RoomRate> findByStartDateBetween = roomRateRepo.findByDayBetween(start, end);
		System.err.println("size: " + findByStartDateBetween.size());

		return findByStartDateBetween;
	}
	
	public List<RoomRate> getAvailableRoomRates(Date start, Date end) { //TODO add a variant of this method but for a particular room 
		List<RoomRate> availableRoomRates = new ArrayList<RoomRate>();
		List<Reservation> inProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
		List<RoomRate> availableRoomRatesForAllRooms = getRoomRatesForAllRooms(start, end);

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

	public Map<Room, List<RoomRate>> getRoomRatesForAllRoomsAsMap(Date startDate, Date endDate) {
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

	public List<RoomRate> getAllRoomRates() { 
		List<RoomRate> target = new ArrayList<RoomRate>();
		roomRateRepo.findAll().forEach(target::add);

		return target;
	}
	
	public Amenity getAmenityById(Long id) {
		return amenityRepo.findOne(id);
	}

	public AmenityType getAmenityTypeById(Long id) {
		return amenityTypeRepo.findOne(id);
	}

	public Room getGetRoomById(Long id) {
		return roomRepo.findOne(id);
	}
	
	public void deleteAmenity(Long id) {
		amenityRepo.delete(id);
	}

	public void deleteAmenityType(Long id) {
		amenityTypeRepo.delete(id);
	}
	
	public void deleteRoomType(Long id) {
		roomTypeRepo.delete(id);
	}

	public void deleteRoom(Long id) {
		roomRepo.delete(id);
	}

	public void deleteRoom(Room room) {
		roomRepo.delete(room);
	}

	public void deleteRoomRate(Long id) {
		roomRateRepo.delete(id);
	}

	public RoomRate getRoomRateById(Long id) {
		return roomRateRepo.findOne(id);
	}
}