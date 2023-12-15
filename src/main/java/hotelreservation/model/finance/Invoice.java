package hotelreservation.model.finance;

import org.hibernate.envers.Audited;

import hotelreservation.model.Guest;
import hotelreservation.model.User;
import hotelreservation.model.enums.InvoiceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;

@Data
@Entity
@Audited
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus invoiceStatus;
	
	@ManyToOne
	private User createdBy;

	@ManyToOne
	private Guest madeOutTo;
	
	//should be a list of payments? So an invoice is made up of a list of payments. 
	//makes sense especially when someone wants to pay half CC and half cash. 
	@OneToOne
	private Payment payment;
	
}
