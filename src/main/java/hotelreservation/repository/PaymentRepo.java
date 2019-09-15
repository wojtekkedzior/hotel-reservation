package hotelreservation.repository;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.finance.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepo extends CrudRepository<Payment, Long>{

	List<Payment> findByReservation(Reservation reservation);
	

	Payment findByReservationAndReservationCharges(Reservation reservation, ReservationCharge reservationCharge);
}
