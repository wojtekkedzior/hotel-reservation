package hotelreservation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Room;
import hotelreservation.model.Status;

@Repository
public interface RoomRepo extends CrudRepository<Room, Long> {
	
	List<Room> findByStatus(Status status);

}
