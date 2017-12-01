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
	
	private double discount;
	
	@ManyToOne
	private User discountAuthorisedBy;
	
	public Reservation() {}
	
	//should we have start and end date here as well?  It would be useful to do so, so that we don't have to iterate over all the roomrates
	//but how would having a start and end date help when dealing with a reservation over non-consecutive days?  this will get complicated
	//logic to determin the reservation type (consecutive days vs non-consecutive) can also get tricky.

}
