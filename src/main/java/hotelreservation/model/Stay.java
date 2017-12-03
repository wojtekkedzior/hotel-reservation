package hotelreservation.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class Stay {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	//Reservation that make up a stay.  This shuld be used to mark a non-consecutive day stay. 
	private List<Reservation> reservations;
	
	//number of days spent at the hotel
	private int duration;
	
	//Start and End date of the entire stay duration, including days spen away from hotel.
	@Temporal(TemporalType.DATE)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	
}
