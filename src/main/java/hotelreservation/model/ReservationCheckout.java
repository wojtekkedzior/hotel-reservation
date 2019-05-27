package hotelreservation.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import hotelreservation.model.finance.Payment;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
public class ReservationCheckout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@OneToOne
	private Reservation reservation;
	
	private String notes;
	
	@NotNull
	private LocalDateTime checkedout;
	
	@NotNull
	@ManyToOne
	private User checkedOutBy;
	
	@OneToOne
	private Payment payment;
	
	//Any payment details such as fully paid? anything outstanding? bar/room charges?

}
