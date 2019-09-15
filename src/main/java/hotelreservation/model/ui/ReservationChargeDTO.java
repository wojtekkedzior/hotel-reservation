package hotelreservation.model.ui;

import hotelreservation.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Data
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public final class ReservationChargeDTO {

	@NotNull
	@OneToOne
	private  Charge charge;
	
	@NotNull
	private int quantity;
	
}
