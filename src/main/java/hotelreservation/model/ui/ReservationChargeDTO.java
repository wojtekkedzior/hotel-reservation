package hotelreservation.model.ui;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.Charge;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
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
