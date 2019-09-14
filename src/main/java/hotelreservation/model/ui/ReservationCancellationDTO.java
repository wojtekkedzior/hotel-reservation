package hotelreservation.model.ui;

import hotelreservation.model.Reservation;
import hotelreservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Immutable
@AllArgsConstructor
public final class ReservationCancellationDTO {

	@NotNull
	@OneToOne
	private Reservation reservation;

	@NotNull
	@NotBlank
	private String reason;

	private User cancelledBy;

	private LocalDateTime cancelledOn;
}
