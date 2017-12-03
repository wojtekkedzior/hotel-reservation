package hotelreservation.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Reservation;

@Repository
public interface ReservationRepo extends CrudRepository<Reservation, Long> {
	
	List<Reservation> findByStartDate(Date startDate);

}
