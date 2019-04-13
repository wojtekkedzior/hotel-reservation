package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
public class ReservationCharge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	//TODO reservationCharge should be a slightly different object, as in without the  reservation. this needs to be fixed as the form can't return a complete reservation
	//what is needed is a separate, UI specific, reservationcharge object. it will be somewhat confusing.
//	@NotNull
	@OneToOne
	private Reservation reservation;
	
	@NotNull
	@OneToOne
	private Charge charge;
	
	@NotNull
	private int quantity;

	public ReservationCharge(Charge charge, int quantity) {
		this.charge = charge;
		this.quantity = quantity;
	}
	
	//date
	//accepted By
	//charged By
}
