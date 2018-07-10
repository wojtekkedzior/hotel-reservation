package hotelreservation.model.finance;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import hotelreservation.model.Charges;
import hotelreservation.model.Reservation;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;

@Entity
@Data
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private PaymentType paymentType;
	
	@OneToMany
	private List<Charges> extraCharges;
	
	@ManyToOne
	private Reservation reservation;

}
