package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.finance.Payment;

public interface PaymentRepo extends CrudRepository<Payment, Long>{

}
