package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Room;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
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
	
	public RoomType createRoomType(RoomType roomType) {
		return roomTypeRepo.save(roomType);
	}

	public List<Room> getAllRooms() {
		Iterable<Room> findAll = roomRepo.findAll();

		List<Room> target = new ArrayList<Room>();
		findAll.forEach(target::add);

		return target;
	}

	public Status createStatus(Status status) {
		return statusRepo.save(status);
	}


	public List<Status> getAllStatuses() {
		Iterable<Status> findAll = statusRepo.findAll();

		List<Status> target = new ArrayList<Status>();
		findAll.forEach(target::add);

		return target;
	}

	public List<RoomType> getAllRoomTypes() {
		Iterable<RoomType> findAll = roomTypeRepo.findAll();

		List<RoomType> target = new ArrayList<RoomType>();
		findAll.forEach(target::add);

		return target;
	}

	public RoomType getRoomTypeById(long id) {
		return roomTypeRepo.findOne(id);
	}

	public void deleteRoomType(RoomType roomType) {
		roomTypeRepo.delete(roomType);
		
	}

	public Room createRoom(Room room) {
		return roomRepo.save(room);
		
	}

	public Room getRoomById(long id) {
		return roomRepo.findOne(id);
	}

	public void deleteRoom(Room room) {
		roomRepo.delete(room);
		
	}

}
