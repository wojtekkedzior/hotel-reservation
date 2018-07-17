package hotelreservation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;

public interface ReservationChargeRepo extends CrudRepository<ReservationCharge, Long>{

	List<ReservationCharge> findByReservation(Reservation reservation);
	

}
