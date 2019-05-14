package hotelreservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import hotelreservation.model.enums.ReservationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
//Arse. Having lombok on Eclipses's classpath causes the equals method to be made up of all the fields regardless of using EqualsAndHashCode. However, running maven clean install with tests 
//causes the correct generation of equals respecting EqualsAndHashCode.  If you run junit tests from eclipse against the code compiled by maven, she works.  
//log a bug with lombok
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor 
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private long id;
	
	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	//TODO need to figure out how to do the occupants and mainguests better
//	@NotNull
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Guest> occupants;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<RoomRate> roomRates;
	
	private double discount;
	
	@ManyToOne
	private User discountAuthorisedBy;
	
	@ManyToOne
	@EqualsAndHashCode.Include
	private User createdBy;
	
	@EqualsAndHashCode.Include
	private LocalDateTime createdOn;
	
	@EqualsAndHashCode.Include
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@EqualsAndHashCode.Include
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	@EqualsAndHashCode.Include
	private ReservationStatus reservationStatus;

	//should we have start and end date here as well?  It would be useful to do so, so that we don't have to iterate over all the roomrates
	//but how would having a start and end date help when dealing with a reservation over non-consecutive days?  this will get complicated
	//logic to determin the reservation type (consecutive days vs non-consecutive) can also get tricky.

	//TODO add cc here
	
	//probably needs a history table
}
