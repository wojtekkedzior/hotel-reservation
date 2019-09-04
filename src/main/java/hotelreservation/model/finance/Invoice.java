package hotelreservation.model.finance;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import hotelreservation.model.Guest;
import hotelreservation.model.User;
import hotelreservation.model.enums.InvoiceStatus;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;

@Data
@Entity
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
