package hotelreservation.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor 
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
	
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyyMMdd") //The DateTimeFormat needs to be in this format for Spring to convert a string back to a date. duh!
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyyMMdd")
	private Date endDate;
	
	@Enumerated(EnumType.STRING)
	private ReservationStatus reservationStatus;
	
	//should we have start and end date here as well?  It would be useful to do so, so that we don't have to iterate over all the roomrates
	//but how would having a start and end date help when dealing with a reservation over non-consecutive days?  this will get complicated
	//logic to determin the reservation type (consecutive days vs non-consecutive) can also get tricky.

	//TODO add cc here
	
	//probably needs a history table

}
