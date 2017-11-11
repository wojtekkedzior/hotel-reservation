package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Room;

@Repository
public interface RoomRepo extends CrudRepository<Room, Long> {

}
