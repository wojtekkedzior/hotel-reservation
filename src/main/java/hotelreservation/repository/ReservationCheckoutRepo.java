package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.ReservationCancellation;

public interface ReservationCheckoutRepo extends CrudRepository<ReservationCancellation, Long>  {

}
