package hotelreservation.model;

import java.time.LocalDateTime;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Stay.class)
public class Stay_ {
	
	public static volatile SingularAttribute<Stay, Long> id;
	public static volatile ListAttribute<Stay, Reservation> reservations;
	public static volatile SingularAttribute<Stay, Integer> duration;
	public static volatile SingularAttribute<Stay, LocalDateTime> startDate;
	public static volatile SingularAttribute<Stay, LocalDateTime> endDate;
}
