package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Guest;

@Repository
public interface GuestRepo extends CrudRepository<Guest, Long> {

}
