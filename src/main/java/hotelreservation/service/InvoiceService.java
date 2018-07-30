package hotelreservation.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.Utils;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Charge;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.finance.Payment;
import hotelreservation.repository.ChargeRepo;
import hotelreservation.repository.PaymentRepo;
import hotelreservation.repository.ReservationChargeRepo;

@Service
@Transactional
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

	public void saveReservationCharge(ReservationCharge charge) {
		reservationChargeRepo.save(charge);
	}
	
	public void savePayment(Payment payment) {
		log.info("Saving payment: " + payment.getId());
		paymentRepo.save(payment);
		log.info("Payment saved: " + payment.getId());
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
	
	public Charge getChargeById(long id) {
		log.info("Looking for Charge with ID: " + id);
		if(chargeRepo.findById(id).isPresent()) {
			return chargeRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public ReservationCharge getReservationChargeById(long id) {
		log.info("Looking for ReservationCharge with ID: " + id);
		if(reservationChargeRepo.findById(id).isPresent()) {
			return reservationChargeRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}		
	}
	
	public Payment getPaymentById(long id) {
		log.info("Looking for Payment with ID: " + id);
		if(paymentRepo.findById(id).isPresent()) {
			return paymentRepo.findById(id).get();
		} else {
			throw new NotFoundException(id);
		}		
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
		if(!chargeRepo.existsById(charge.getId())) {
			throw new NotDeletedException(charge.getId());
		}
		chargeRepo.delete(charge);
	}
	
	public void deleteReservationCharge(ReservationCharge reservationCharge) {
		if(!reservationChargeRepo.existsById(reservationCharge.getId())) {
			throw new NotDeletedException(reservationCharge.getId());
		}
		reservationChargeRepo.delete(reservationCharge);
	}
	
	public void deletePayment(Payment payment) {
		if(!paymentRepo.existsById(payment.getId())) {
			throw new NotDeletedException(payment.getId());
		}
		paymentRepo.delete(payment);
	}
}