package hotelreservation.repository;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationChargeRepo extends CrudRepository<ReservationCharge, Long>{

	List<ReservationCharge> findByReservation(Reservation reservation);
	

}
