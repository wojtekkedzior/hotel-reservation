package hotelreservation.model;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Audited
public class Stay {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	//Reservation that make up a stay.  This should be used to mark a non-consecutive day stay.
	@NotNull
	@ManyToMany
	@NotAudited
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
