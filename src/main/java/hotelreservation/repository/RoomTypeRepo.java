package hotelreservation.repository;

import hotelreservation.model.RoomType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepo extends CrudRepository<RoomType, Long> {

}
