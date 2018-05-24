package hotelreservation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.finance.Payment;
import hotelreservation.repository.PaymentRepo;

@Service
public class InvoiceService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PaymentRepo paymentRepo;

	public void savePayment(Payment payment) {
		log.info("Saving payment: " + payment.getId());
		paymentRepo.save(payment);
	}

}
