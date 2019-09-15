package hotelreservation.repository;

import hotelreservation.model.ReservationCheckout;
import org.springframework.data.repository.CrudRepository;

public interface ReservationCheckoutRepo extends CrudRepository<ReservationCheckout, Long>  {

}
