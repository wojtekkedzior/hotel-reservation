package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.ReservationCheckout;

public interface ReservationCancellationRepo extends CrudRepository<ReservationCheckout, Long>  {

}
