package hotelreservation.service;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.*;
import hotelreservation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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
		
		List<AmenityType> amenityTypes = utils.toList(amenityTypeRepo.findAll());
		
		amenityTypes.stream()
				.filter(t -> t.getName().equals("Hotel"))
				.forEach(t -> roomAmenities.addAll(amenityRepo.findByAmenityType(t)));

		return roomAmenities;
	}

	public List<Room> getByRoomsByStatus(Status status) {
		return utils.toList(roomRepo.findByStatus(status));
	}

	public List<RoomRate> getRoomRates(LocalDate start, LocalDate end) {
		if(end.isBefore(start)) {
			throw new MissingOrInvalidArgumentException("End date:" + end + " cannot be before start date: " + start);
		}

		List<RoomRate> findByDayBetween = start.isEqual(end) ? roomRateRepo.findByDay(start) : roomRateRepo.findByDayBetween(start, end.minus(Period.ofDays(1)));
		log.info("Looking for all RoomRates between: {} and: {} -  Found: {}", start, end, findByDayBetween.size());
		return findByDayBetween;
	}
	
	public List<RoomRate> getRoomRates(Room room, LocalDate start, LocalDate end) {
		List<RoomRate> findByStartDateBetween = roomRateRepo.findByRoomIdAndDayBetween(room.getId(), start, end);
		log.info("Looking for all RoomRates between: {} and: {} for Room: {} - Found: {}", start, end, room.getId(), findByStartDateBetween.size());
		return findByStartDateBetween;
	}
	
	public List<RoomRate> getAvailableRoomRates(LocalDate start, LocalDate end) { //TODO add a variant of this method but for a particular room //TODO finally figure out how to use a join and apply it here
		List<RoomRate> availableRoomRates = new ArrayList<>();
		List<Reservation> activeReservation = reservationRepo.findInProgressAndUpComingReservations();
		List<RoomRate> availableRoomRatesForAllRooms = getRoomRates(start, end);
		
		for (RoomRate roomRate : availableRoomRatesForAllRooms) {
			if(activeReservation.stream().noneMatch(r -> r.getRoomRates().contains(roomRate))) {
				availableRoomRates.add(roomRate);
			}
		}

		return availableRoomRates;
	}

	public Map<Room, List<RoomRate>> getRoomRatesAsMap(LocalDate startDate, LocalDate endDate) {
		Map<Room, List<RoomRate>> ratesForAllRooms = new HashMap<>();
		
		for (RoomRate roomRate : getAvailableRoomRates(startDate, endDate)) {
			ratesForAllRooms.computeIfAbsent(roomRate.getRoom(), k -> new ArrayList<>()).add(roomRate);
		}
		
		return ratesForAllRooms;
	}
	
	//---- Amenity
	public Amenity saveAmenity(Amenity amenity) {
		return amenityRepo.save(amenity);
	}
	
	public Amenity getAmenityById(long id) {
		log.info("Looking for Amenity with ID: {}", id);
		return amenityRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public List<Amenity> getAllAmenities() {
		return utils.toList(amenityRepo.findAll());
	}
	
	public void deleteAmenity(long id) {
		if(!amenityRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		amenityRepo.deleteById(id);
	}
	
	//---- AmenityType
	public AmenityType saveAmenityType(AmenityType amenityType) {
		return amenityTypeRepo.save(amenityType);
	}
	
	public AmenityType getAmenityTypeById(long id) {
		log.info("Looking for AmenityType with ID: {}", id);
		return amenityTypeRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public List<AmenityType> getAllAmenityTypes() {
		return utils.toList(amenityTypeRepo.findAll());
	}
	
	public void deleteAmenityType(long id) {
		if(!amenityTypeRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		amenityTypeRepo.deleteById(id);
	}
	
	//---- Room
	public Room saveRoom(Room room) {
		room.setCreatedOn(LocalDateTime.now());
		return roomRepo.save(room);
	}
	
	public Room getRoomById(long id) {
		log.info("Looking for Room with ID: {}", id);
		return roomRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}
	
	public List<Room> getAllRooms() {
		return utils.toList(roomRepo.findAll());
	}
	
	public void deleteRoomById(long id) {
		if(!roomRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		roomRepo.deleteById(id);
	}
	
	public void deleteRoom(Room room) {
		if(!roomRepo.existsById(room.getId())) {
			throw new NotDeletedException(room.getId());
		}
		roomRepo.delete(room);
	}
	
	//---- RoomType
	public RoomType saveRoomType(RoomType roomType) {
		return roomTypeRepo.save(roomType);
	}
	
	public RoomType getRoomTypeById(long id) {
		log.info("Looking for RoomType with ID: {}", id);
		return roomTypeRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}
	
	public List<RoomType> getAllRoomTypes() {
		return utils.toList(roomTypeRepo.findAll());
	}
	
	public void deleteRoomType(Long id) {
		if(!roomTypeRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		roomTypeRepo.deleteById(id);
	}
	
	//---- Room Rate
	public RoomRate saveRoomRate(RoomRate roomRate) {
		return roomRateRepo.save(roomRate);
	}

	public RoomRate getRoomRateById(long id) {
		log.info("Looking for RoomRate with ID: {}", id);
		return roomRateRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}

	public List<RoomRate> getAllRoomRates() { 
		return utils.toList(roomRateRepo.findAll());
	}
	
	public void deleteRoomRate(long id) {
		if(!roomRateRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		roomRateRepo.deleteById(id);
	}
	
	//---- Status
	public Status saveStatus(Status status) {
		return statusRepo.save(status);
	}
	
	public Status getStatusById(long id) {
		log.info("Looking for Status with ID: {} ", id);
		return statusRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}

	public List<Status> getAllStatuses() {
		return utils.toList(statusRepo.findAll());
	}
	
	public void deleteStatus(long id) {
		if(!statusRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}
		statusRepo.deleteById(id);
	}

	public long getRoomsCount() {
		return roomRepo.count();
	}

	public long getRoomRateCount() {
		return roomRateRepo.count();
	}
	
	/**
	 * Returns a Map of available room rates as a list for each day in the range provided. Any missing days are replaced with null so that the .size() of each list is the same.
	 * Available rates will also always be in the same index across all lists.
	 * 
	 * The returned map is sorted by the room id (this will be changed later to room number) so that the UI can display the rooms ascending. 
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Map<LocalDate, List<RoomRate>> getRoomRatesPerDate(LocalDate start, LocalDate end) {
		Map<LocalDate, List<RoomRate>> roomRatesAsMapByDates = new TreeMap<>(); 
		List<RoomRate> availableRoomRates = getAvailableRoomRates(start, end); //this method is wrong. for the 13th to the 15th it should only return rates for the 13th and 14th

		Map<Room, List<RoomRate>> roomRatesPerRoom = availableRoomRates.stream()
			.collect(Collectors.groupingBy(RoomRate::getRoom, TreeMap::new, Collectors.toList()));

		start.datesUntil(end).forEach(day -> {
			for (Map.Entry<Room, List<RoomRate>> room : roomRatesPerRoom.entrySet()) {
				boolean roomRateFound = false;

				for (RoomRate roomRate : roomRatesPerRoom.get(room.getKey())) {
					if (roomRate.getDay().isEqual(day)) {
						roomRatesAsMapByDates.computeIfAbsent(day, k -> new LinkedList<>()).add(roomRate);
						roomRateFound = true;
						break;
					}
				}

				if (!roomRateFound) {
					roomRatesAsMapByDates.computeIfAbsent(day, k -> new LinkedList<>()).add(null);
				}
			}
		});

		return roomRatesAsMapByDates;
	}
}