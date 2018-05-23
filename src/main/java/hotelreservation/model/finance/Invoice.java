package hotelreservation.model.finance;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import hotelreservation.model.Guest;
import hotelreservation.model.User;
import hotelreservation.model.enums.InvoiceStatus;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;

@Data
@Entity
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus invoiceStatus;
	
	@ManyToOne
	private User createdBy;
	
/*	@ManyToOne
	private User settledBy;
	
	//potentiall not needed as there should be an audit log.
	@ManyToOne
	private User adjustedBy;*/
	
	@ManyToOne
	private Guest madeOutTo;
	
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	private Payment payment;
	
}
