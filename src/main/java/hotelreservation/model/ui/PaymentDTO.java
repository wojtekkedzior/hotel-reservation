package hotelreservation.model.ui;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Immutable
public final class PaymentDTO {
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@NotNull
	private List<ReservationCharge> reservationCharges;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;

}
