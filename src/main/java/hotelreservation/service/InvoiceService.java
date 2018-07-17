package hotelreservation.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.finance.Payment;
import hotelreservation.repository.PaymentRepo;
import hotelreservation.repository.ReservationChargeRepo;

@Service
public class InvoiceService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	private ReservationChargeRepo reservationChargeRepo;

	public void savePayment(Payment payment) {
		log.info("Saving payment: " + payment.getId());
		paymentRepo.save(payment);
	}

	public void saveChargeToReservation(ReservationCharge charge) {
		reservationChargeRepo.save(charge);
	}

	public List<ReservationCharge> getAllReservationCharges(Reservation reservation) {
		return reservationChargeRepo.findByReservation(reservation);
	}

	public List<Payment> getAllPayments(Reservation reservation) {
		return paymentRepo.findByReservation(reservation);
	}

}
