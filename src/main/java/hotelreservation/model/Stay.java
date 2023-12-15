package hotelreservation.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

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
