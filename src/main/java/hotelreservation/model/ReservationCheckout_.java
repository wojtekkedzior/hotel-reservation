package hotelreservation.model;

import java.time.LocalDateTime;

import hotelreservation.model.finance.Payment;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ReservationCheckout.class)
public class ReservationCheckout_ {
	
	
	public static volatile SingularAttribute<ReservationCheckout, Long> id;
	public static volatile SingularAttribute<ReservationCheckout, Reservation> reservation;
	public static volatile SingularAttribute<ReservationCheckout, String> notes;
	public static volatile SingularAttribute<ReservationCheckout, LocalDateTime> checkedout;
	public static volatile SingularAttribute<ReservationCheckout, User> checkedOutBy;
	public static volatile SingularAttribute<ReservationCheckout, Payment> payment;

}
