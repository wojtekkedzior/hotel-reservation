package hotelreservation.model.ui;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import hotelreservation.model.Charge;
import lombok.Data;

@Data
public class ReservationChargeDTO {

	@NotNull
	private long reservationId;
	
	@NotNull
	@OneToOne
	private Charge charge;
	
	@NotNull
	private int quantity;
	
}
