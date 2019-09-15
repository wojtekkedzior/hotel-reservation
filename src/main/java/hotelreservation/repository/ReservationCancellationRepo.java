package hotelreservation.repository;

import hotelreservation.model.ReservationCancellation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationCancellationRepo extends CrudRepository<ReservationCancellation, Long>  {

}
