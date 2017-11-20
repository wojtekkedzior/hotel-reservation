package hotelreservation.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
	private Guest mainGuest;
	
	@ManyToMany
	private List<Guest> occupants;
	
	@ManyToMany
	private List<RoomRate> roomRates;
	
	public Reservation() {}

}
