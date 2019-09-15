package hotelreservation.repository;

import hotelreservation.model.Guest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepo extends CrudRepository<Guest, Long> {

}
