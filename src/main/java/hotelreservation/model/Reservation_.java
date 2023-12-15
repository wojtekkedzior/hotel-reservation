package hotelreservation.model;

import java.time.LocalDateTime;

import hotelreservation.model.enums.ReservationStatus;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Reservation.class)
public class Reservation_ {
	
	public static volatile SingularAttribute<Reservation, Long> id;
	public static volatile SingularAttribute<Reservation, String> firstName;
	public static volatile SingularAttribute<Reservation, String> lastName;
	
	public static volatile ListAttribute<Reservation, Guest> occupants;
	public static volatile ListAttribute<Reservation, RoomRate> roomRates;
	
	public static volatile SingularAttribute<Reservation, Double> discount;
	public static volatile SingularAttribute<Reservation, User> discountAuthorisedBy;
	public static volatile SingularAttribute<Reservation, User> createdBy;
	public static volatile SingularAttribute<Reservation, LocalDateTime> createdOn;
	public static volatile SingularAttribute<Reservation, LocalDateTime> startDate;
	public static volatile SingularAttribute<Reservation, LocalDateTime> endDate;
	public static volatile SingularAttribute<Reservation, ReservationStatus> reservationStatus;
	
	
//	//TODO need to figure out how to do the occupants and mainguests better
////	@NotNull
//	@ManyToMany
//	@LazyCollection(LazyCollectionOption.FALSE)
//	@Exclude
//	private List<Guest> occupants;
//	
//	@ManyToMany
//	@LazyCollection(LazyCollectionOption.FALSE)
//	@Exclude
//	private List<RoomRate> roomRates;
	
	
}
