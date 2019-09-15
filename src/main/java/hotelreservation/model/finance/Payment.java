package hotelreservation.model.finance;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Audited
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@NotNull
	@OneToMany
	private List<ReservationCharge> reservationCharges;
	
	@NotNull
	@ManyToOne
	@NotAudited
	private Reservation reservation;
	
	private LocalDateTime paymentDate;
	
	public int getSubTotal() {
		int subTotal = 0;
		
		for (ReservationCharge reservationCharge : reservationCharges) {
			subTotal = subTotal + (reservationCharge.getQuantity() * reservationCharge.getCharge().getValue());
		}
		
		return subTotal;
	}

}