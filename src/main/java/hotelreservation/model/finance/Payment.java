package hotelreservation.model.finance;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;

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