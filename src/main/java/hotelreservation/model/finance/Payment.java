package hotelreservation.model.finance;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;

@Entity
@Data
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	private PaymentType paymentType;
	
	@NotNull
	@OneToMany
	private List<ReservationCharge> reservationCharges;
	
	//todo again, need another ui object to represent a payment becuase now it can't resolve a full reservation
	//@NotNull
	@ManyToOne
	private Reservation reservation;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	
	public int getSubTotal() {
		int subTotal = 0;
		
		for (ReservationCharge reservationCharge : reservationCharges) {
			subTotal = subTotal + (reservationCharge.getQuantity() * reservationCharge.getCharge().getValue());
		}
		
		return subTotal;
	}

}
