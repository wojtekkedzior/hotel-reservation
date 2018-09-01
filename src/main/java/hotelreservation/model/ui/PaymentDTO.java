package hotelreservation.model.ui;

import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import lombok.Data;

@Data
/**
 * Reason this class is not an entity is that it's purely used in the controller as a form field. The Payment model should have a reference to a reference
 * @author wojtek
 *
 */
public class PaymentDTO {
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@NotNull
	private List<ReservationCharge> reservationCharges;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	
	@NotNull
	private long reservationId;
	
	public PaymentDTO () {}

}
