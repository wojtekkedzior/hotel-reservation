package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.Utils;
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
	
	@Autowired
	private Utils utils;
	
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
		return utils.toList(chargeRepo.findAll());
	}
	
	public List<ReservationCharge> getAllReservationCharges() {
		return utils.toList(reservationChargeRepo.findAll());
	}
	
	public List<Payment> getAllPayments() {
		return utils.toList(paymentRepo.findAll());
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