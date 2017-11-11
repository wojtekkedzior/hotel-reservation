package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Status;

@Repository
public interface StatusRepo extends CrudRepository<Status, Long> {

}
