package hotelreservation.repository.finance;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.finance.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

}
