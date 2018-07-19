package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Charge;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
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
	
	public void createCharge(Charge charge) {
		chargeRepo.save(charge);
	}

	public void createReservationCharge(ReservationCharge charge) {
		reservationChargeRepo.save(charge);
	}
	
	public void savePayment(Payment payment) {
		log.info("Saving payment: " + payment.getId());
		paymentRepo.save(payment);
	}
	
	public List<Charge> getAllCharges() {
		List<Charge> target = new ArrayList<Charge>();
		chargeRepo.findAll().forEach(target::add);
		return target;
	}
	
	public List<ReservationCharge> getAllReservationCharges() {
		List<ReservationCharge> target = new ArrayList<ReservationCharge>();
		reservationChargeRepo.findAll().forEach(target::add);
		return target;
	}
	
	public List<Payment> getAllPayments() {
		List<Payment> target = new ArrayList<Payment>();
		paymentRepo.findAll().forEach(target::add);
		return target;
	}

	public List<ReservationCharge> getAllReservationChargesForAReservation(Reservation reservation) {
		return reservationChargeRepo.findByReservation(reservation);
	}

	public List<Payment> getAllPaymentsForReservation(Reservation reservation) {
		return paymentRepo.findByReservation(reservation);
	}

	public List<ReservationCharge> getOutstandingCharges(Reservation reservation) {
		List<ReservationCharge> charges = new ArrayList<ReservationCharge>();
		
		for (ReservationCharge reservationCharge : getAllReservationChargesForAReservation(reservation)) {
			Payment payment = paymentRepo.findByReservationAndReservationCharges(reservation, reservationCharge);
			
			if(payment == null) {
				charges.add(reservationCharge);
			} 
		}
		
		return charges;
	}

	public void deleteCharge(Charge charge) {
		chargeRepo.delete(charge);
	}
	
	public void deleteReservationCharge(ReservationCharge reservationCharge) {
		reservationChargeRepo.delete(reservationCharge);
	}
	
	public void deletePayment(Payment payment) {
		paymentRepo.delete(payment);
	}
}