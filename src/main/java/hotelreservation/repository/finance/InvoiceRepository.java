package hotelreservation.repository.finance;

import hotelreservation.model.finance.Invoice;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

}
