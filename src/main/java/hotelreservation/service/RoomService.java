package hotelreservation.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.repository.AmenityRepo;
import hotelreservation.repository.AmenityTypeRepo;
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

	public void deleteRoom(Room room) {
		roomRepo.delete(room);
	}

	public void createAmenityType(AmenityType amenityType) {
		amenityTypeRepo.save(amenityType);
	}

	public void createAmenity(Amenity ammenity) {
		amenityRepo.save(ammenity);
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

	public void createRoomRate(RoomRate roomRate) {
		roomRateRepo.save(roomRate);
	}

	public List<RoomRate> getAvailableRoomRatesForRoom(Date start, Date end) {
		List<RoomRate> findByStartDateBetween = roomRateRepo.findByDayBetween(start, end);
		System.err.println("size: " + findByStartDateBetween.size());
		
		return findByStartDateBetween;
	}
	
	//TODO this needs to check if these rooms are actually available
	public Map<Room, List<RoomRate>> getRoomRatesForAllRooms(Date startDate, Date endDate) {
		Map<Room, List<RoomRate>> ratesForAllRooms = new HashMap<Room, List<RoomRate>>();
		
		Iterable<Room> allRooms = roomRepo.findAll();
		
		for (Room room : allRooms) {
			ratesForAllRooms.put(room, roomRateRepo.findByRoomIdAndDayBetween(room.getId(), startDate, endDate));
		}
		
		return ratesForAllRooms;
	}
	
	public List<RoomRate> getAvailableRoomRates() {
		List<RoomRate> target = new ArrayList<RoomRate>();
		roomRateRepo.findAll().forEach(target::add);

		return target;
	}
	
	
	
	
	
	//TODO refactor this as its duplicated. some it can also be moved into the sprint properties file
	public Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}