package hotelreservation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.finance.Payment;

public interface PaymentRepo extends CrudRepository<Payment, Long>{

	List<Payment> findByReservation(Reservation reservation);
	

	Payment findByReservationAndReservationCharges(Reservation reservation, ReservationCharge reservationCharge);
}
