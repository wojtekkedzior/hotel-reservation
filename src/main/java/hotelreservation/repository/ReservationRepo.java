package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Reservation;

@Repository
public interface ReservationRepo extends CrudRepository<Reservation, Long> {

}
