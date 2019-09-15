package hotelreservation.repository;

import hotelreservation.model.Room;
import hotelreservation.model.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends CrudRepository<Room, Long> {
	
	List<Room> findByStatus(Status status);

}
