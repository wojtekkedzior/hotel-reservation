package hotelreservation.service;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Charge;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.finance.Payment;
import hotelreservation.repository.ChargeRepo;
import hotelreservation.repository.PaymentRepo;
import hotelreservation.repository.ReservationChargeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {
	private final PaymentRepo paymentRepo;
	private final ReservationChargeRepo reservationChargeRepo;
	private final ChargeRepo chargeRepo;
	private final Utils utils;

	public void saveCharge(Charge charge) {
		chargeRepo.save(charge);
	}

	public void saveReservationCharge(ReservationCharge charge) {
		Reservation reservation = charge.getReservation();

		if (reservation != null && !reservation.getReservationStatus().equals(ReservationStatus.IN_PROGRESS)) {
			log.warn("Reservation:{} was in wrong state to create charge: {}", reservation.getId(), reservation.getReservationStatus());
			throw new MissingOrInvalidArgumentException("Reservation in wrong status: " + reservation.getId() + " " + reservation.getReservationStatus());
		}

		reservationChargeRepo.save(charge);
	}

	public void savePayment(Payment payment) {
		log.info("Saving payment: {}", payment.getId());
		paymentRepo.save(payment);
		log.info("Payment saved: {}", payment.getId());
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
		log.info("Looking for Charge with ID: {}", id);
		return chargeRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}

	public ReservationCharge getReservationChargeById(long id) {
		log.info("Looking for ReservationCharge with ID: {}", id);
		return reservationChargeRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}

	public Payment getPaymentById(long id) {
		log.info("Looking for Payment with ID: {}", id);
		return paymentRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
	}

	public List<ReservationCharge> getAllReservationChargesForAReservation(Reservation reservation) {
		return reservationChargeRepo.findByReservation(reservation);
	}

	public List<Payment> getAllPaymentsForReservation(Reservation reservation) {
		return paymentRepo.findByReservation(reservation);
	}

	public List<ReservationCharge> getOutstandingCharges(Reservation reservation) {
		List<ReservationCharge> charges = new ArrayList<>();

		for (ReservationCharge reservationCharge : getAllReservationChargesForAReservation(reservation)) {
			Payment payment = paymentRepo.findByReservationAndReservationCharges(reservation, reservationCharge);

			if (payment == null) {
				charges.add(reservationCharge);
			}
		}

		return charges;
	}

	public void deleteCharge(Charge charge) {
		if (!chargeRepo.existsById(charge.getId())) {
			throw new NotDeletedException(charge.getId());
		}
		chargeRepo.delete(charge);
	}

	public void deleteReservationCharge(ReservationCharge reservationCharge) {
		if (!reservationChargeRepo.existsById(reservationCharge.getId())) {
			throw new NotDeletedException(reservationCharge.getId());
		}
		reservationChargeRepo.delete(reservationCharge);
	}

	public void deletePayment(Payment payment) {
		if (!paymentRepo.existsById(payment.getId())) {
			throw new NotDeletedException(payment.getId());
		}
		paymentRepo.delete(payment);
	}

	public boolean areAllChargesPaidFor(Reservation reservation) {
		return getOutstandingCharges(reservation).isEmpty();
	}

	public long getTotalOfOutstandingCharges(List<Reservation> reservationsInProgress) {
		final AtomicReference<Long> reference = new AtomicReference<>(0L);

		reservationsInProgress.forEach(reservation ->
			reference.accumulateAndGet(getOutstandingCharges(reservation).stream()
					.mapToLong(x -> x.getQuantity() * x.getCharge().getValue())
					.sum(), Math::addExact)
		);

		return reference.get();
	}
}