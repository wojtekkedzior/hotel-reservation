package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class ReservationCharge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne
	private Reservation reservation;
	
	@OneToOne
	private Charge charge;
	
	private int quantity;

	public ReservationCharge() {}
	
	public ReservationCharge(Charge charge, int quantity) {
		this.charge = charge;
		this.quantity = quantity;
	}
	
	//date
	//accepted By
	//charged By
}
