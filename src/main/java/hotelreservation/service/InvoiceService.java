package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import hotelreservation.model.Charge;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.Room;
import hotelreservation.model.finance.Payment;
import hotelreservation.repository.ChargeRepo;
import hotelreservation.repository.PaymentRepo;
import hotelreservation.repository.ReservationChargeRepo;

@Service
public class InvoiceService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	private ReservationChargeRepo reservationChargeRepo;

	@Autowired
	private ChargeRepo chargeRepo;

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
		List<Payment> payemnts = paymentRepo.findByReservation(reservation); 
//		System.err.println(payemnts);
		
		return payemnts;
	}

	public List<ReservationCharge> getOutstandingCharges(Reservation reservation) {
		List<ReservationCharge> charges = new ArrayList<ReservationCharge>();
		
//		List<Payment> payments = getAllPayments(reservation);
		List<ReservationCharge> chargesForReservation = getAllReservationCharges(reservation);
		
		for (ReservationCharge reservationCharge : chargesForReservation) {
			Payment p = paymentRepo.findByReservationAndReservationCharges(reservation, reservationCharge);
			
			if(p == null) {
				charges.add(reservationCharge);
			} 
		}
		
		return charges;
	}

	public void createCharge(Charge charge) {
		chargeRepo.save(charge);
	}

	public List<Charge> getAllCharges() {
		List<Charge> target = new ArrayList<Charge>();
		chargeRepo.findAll().forEach(target::add);

		return target;
	}
}