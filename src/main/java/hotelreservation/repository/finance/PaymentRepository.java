package hotelreservation.repository.finance;

import hotelreservation.model.finance.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

}
