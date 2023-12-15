package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ReservationCharge.class)
public class ReservationCharge_ {
	
	public static volatile SingularAttribute<ReservationCharge, Long> id;
	public static volatile SingularAttribute<ReservationCharge, Reservation> reservation;
	public static volatile SingularAttribute<ReservationCharge, Charge> charge;
	public static volatile SingularAttribute<ReservationCharge, Integer> quantity;

	//TODO reservationCharge should be a slightly different object, as in without the  reservation. this needs to be fixed as the form can't return a complete reservation
	//what is needed is a separate, UI specific, reservationcharge object. it will be somewhat confusing.
//	@NotNull
//	@OneToOne
//	@NotAudited
//	private Reservation reservation;
//	
	//date
	//accepted By
	//charged By
}
