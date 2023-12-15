package hotelreservation.model;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import hotelreservation.model.finance.Payment;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Audited
public class ReservationCheckout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@OneToOne
	@NotAudited
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
