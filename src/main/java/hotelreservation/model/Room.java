package hotelreservation.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private int roomNumber;
	
	@ManyToOne
	private Status status; //as in 'in operation' 'in maintenace' etc
	
	private String name;
	private String description;
	
	@ManyToMany
	private List<Amenity> roomAmenities;
	
	@ManyToOne
	private RoomType roomType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn; 
	
	@ManyToOne
	private User createdBy;

	public Room() {}
	
	public Room(int roomNumber, Status status, RoomType roomType, User createdBy) {
		this.roomNumber = roomNumber;
		this.status = status;
		this.roomType = roomType;
		this.createdBy = createdBy;
	}
	
}
