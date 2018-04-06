package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.ReservationCancellation;

public interface ReservationCancellationRepo extends CrudRepository<ReservationCancellation, Long>  {

}
