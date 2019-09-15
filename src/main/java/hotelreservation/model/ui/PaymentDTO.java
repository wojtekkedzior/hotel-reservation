package hotelreservation.model.ui;

import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
/**
 * Reason this class is not an entity is that it's purely used in the controller as a form field. The Payment model should have a reference to a reference
 * @author wojtek
 *
 */
@Immutable
@RequiredArgsConstructor
public final class PaymentDTO {
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@NotNull
	private List<ReservationCharge> reservationCharges;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;

}
