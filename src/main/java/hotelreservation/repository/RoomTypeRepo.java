package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.RoomType;

@Repository
public interface RoomTypeRepo extends CrudRepository<RoomType, Long> {

}
