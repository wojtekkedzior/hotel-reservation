package hotelreservation.model.ui;

import org.springframework.data.annotation.Immutable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Immutable
@AllArgsConstructor
public final class ReservationCancellationDTO {

	@NotNull
	@NotBlank
	private String reason;
}
