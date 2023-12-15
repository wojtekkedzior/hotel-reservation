package hotelreservation.model;

import java.time.LocalDateTime;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ReservationCancellation.class)
public class ReservationCancellation_ {
	
	public static volatile SingularAttribute<ReservationCancellation, Long> id;
	
	public static volatile SingularAttribute<ReservationCancellation, Reservation> reservation;
	public static volatile SingularAttribute<ReservationCancellation, String> reason;
	public static volatile SingularAttribute<ReservationCancellation, User> cancelledBy;
	public static volatile SingularAttribute<ReservationCancellation, LocalDateTime> cancelledOn;
}
