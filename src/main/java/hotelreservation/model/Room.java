package hotelreservation.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
	
	@OneToOne
	private Status status; //as in 'in operation' 'in maintenace' etc
	private String name;
	private String description;
	
	@OneToMany
	private List<Ammenity> roomAmmenities;
	
	@OneToOne
	private RoomType roomType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn; 
	
	@OneToOne
	private User createdBy;
	
}
