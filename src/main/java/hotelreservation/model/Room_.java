package hotelreservation.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import hotelreservation.model.enums.ReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@StaticMetamodel(Room.class)
public class Room_ {
	public static volatile SingularAttribute<Room, Long> id;
	public static volatile SingularAttribute<Room, Integer> roomNumber;
	public static volatile SingularAttribute<Room, Status> status;
	
	public static volatile SingularAttribute<Room, String> name;
	public static volatile SingularAttribute<Room, String> description;

	public static volatile ListAttribute<Room, Amenity> roomAmenities;
	
	public static volatile SingularAttribute<Room, RoomType> roomType;
	
	public static volatile SingularAttribute<Room, User> createdBy;
	public static volatile SingularAttribute<Room, LocalDateTime> createdOn;

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Exclude
//	private long id;
//	
//	@NotNull
//	private int roomNumber;
//	
//	@NotNull
//	@ManyToOne
//	private Status status; //as in 'in operation' 'in maintenace' etc
//	
//	private String name;
//	private String description;
//	
//	@ManyToMany
//	@LazyCollection(LazyCollectionOption.FALSE)
//	private List<Amenity> roomAmenities;
//	
//	@NotNull
//	@ManyToOne
//	private RoomType roomType;
//	
//	@NotNull
//	private LocalDateTime createdOn; 
//	
//	@NotNull
//	@ManyToOne
//	private User createdBy;
//
//	public Room_(int roomNumber, Status status, RoomType roomType, User createdBy) {
//		this.roomNumber = roomNumber;
//		this.status = status;
//		this.roomType = roomType;
//		this.createdBy = createdBy;
//		createdOn = LocalDateTime.now();
//	}
//
//	@Override
//	public int compareTo(Room_ arg0) {
//		return (int)(this.id - arg0.getId());
//	}
}
