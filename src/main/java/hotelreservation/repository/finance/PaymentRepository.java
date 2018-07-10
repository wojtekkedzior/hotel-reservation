package hotelreservation.repository.finance;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.finance.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
