package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class ReservationCharge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@OneToOne
	private Reservation reservation;
	
	@NotNull
	@OneToOne
	private Charge charge;
	
	@NotNull
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
