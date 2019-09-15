package hotelreservation.model;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@Audited
public class ReservationCharge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;

	//TODO reservationCharge should be a slightly different object, as in without the  reservation. this needs to be fixed as the form can't return a complete reservation
	//what is needed is a separate, UI specific, reservationcharge object. it will be somewhat confusing.
//	@NotNull
	@OneToOne
	@NotAudited
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
