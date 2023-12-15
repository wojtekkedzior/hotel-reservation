package hotelreservation.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room implements Comparable<Room> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	private int roomNumber;
	
	@NotNull
	@ManyToOne
	private Status status; //as in 'in operation' 'in maintenace' etc
	
	private String name;
	private String description;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Amenity> roomAmenities;
	
	@NotNull
	@ManyToOne
	private RoomType roomType;
	
	@NotNull
	private LocalDateTime createdOn; 
	
	@NotNull
	@ManyToOne
	private User createdBy;

	public Room(int roomNumber, Status status, RoomType roomType, User createdBy) {
		this.roomNumber = roomNumber;
		this.status = status;
		this.roomType = roomType;
		this.createdBy = createdBy;
		createdOn = LocalDateTime.now();
	}

	@Override
	public int compareTo(Room arg0) {
		return (int)(this.id - arg0.getId());
	}
}
