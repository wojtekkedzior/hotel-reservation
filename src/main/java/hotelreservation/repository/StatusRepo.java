package hotelreservation.repository;

import hotelreservation.model.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepo extends CrudRepository<Status, Long> {

}
