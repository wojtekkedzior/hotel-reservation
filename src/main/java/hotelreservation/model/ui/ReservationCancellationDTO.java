package hotelreservation.model.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Immutable
@AllArgsConstructor
public final class ReservationCancellationDTO {

	@NotNull
	@NotBlank
	private String reason;
}
