package hotelreservation.model.ui;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
