package hotelreservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import hotelreservation.model.enums.ReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
//Arse. Having lombok on Eclipses's classpath causes the equals method to be made up of all the fields regardless of using EqualsAndHashCode. However, running maven clean install with tests 
//causes the correct generation of equals respecting EqualsAndHashCode.  If you run junit tests from eclipse against the code compiled by maven, she works.  
//log a bug with lombok
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	//TODO need to figure out how to do the occupants and mainguests better
//	@NotNull TODO: A list the occupants must not be null. otherwise who is the booking for?
	
	@ManyToMany (fetch = FetchType.EAGER)
	@Exclude
	private List<Guest> occupants;
	
	@ManyToMany (fetch = FetchType.EAGER)
	@Exclude
//	TODO the roomrates list but be not null.  What is the point of creating a reservation without rooms (which means room rates)?
	private List<RoomRate> roomRates;
	
	//TODO a discount needs to be an object of its own and it needs a 'type of discount'.  Maybe it could/should could be part of a ReservationCharge?
	private double discount;
	
	@ManyToOne
	@Exclude
	private User discountAuthorisedBy;
	
	@ManyToOne
	private User createdBy;
	
	private LocalDateTime createdOn;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	private ReservationStatus reservationStatus;

	//should we have start and end date here as well?  It would be useful to do so, so that we don't have to iterate over all the roomrates
	//but how would having a start and end date help when dealing with a reservation over non-consecutive days?  this will get complicated
	//logic to determin the reservation type (consecutive days vs non-consecutive) can also get tricky.

	//TODO add cc here
	
	//probably needs a history table
}
