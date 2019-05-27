package hotelreservation.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
public class Stay {
	
	//TODO don't worry about the stay for now. 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	//Reservation that make up a stay.  This shuld be used to mark a non-consecutive day stay. 
	@NotNull
	@ManyToMany
	private List<Reservation> reservations; 
	
	@NotBlank
	//number of days spent at the hotel
	private int duration;
	
	//Start and End date of the entire stay duration, including days spen away from hotel.
	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate endDate;
}
