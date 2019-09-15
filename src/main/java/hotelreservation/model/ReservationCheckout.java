package hotelreservation.model;

import hotelreservation.model.finance.Payment;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
