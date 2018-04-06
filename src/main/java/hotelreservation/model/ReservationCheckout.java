package hotelreservation.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class ReservationCheckout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne
	private Reservation reservation;
	
	private String notes;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkedout;
	
	@ManyToOne
	private User checkedOutBy;
	
	//Any payment details
}
