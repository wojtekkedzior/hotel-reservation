package hotelreservation.model.ui;

import hotelreservation.model.Guest;
import hotelreservation.model.User;
import hotelreservation.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Immutable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Immutable
@AllArgsConstructor
public final class ReservationDTO {

	@NotNull
	private String firstName;

	@NotNull
	private String lastName;

	private List<Guest> occupants;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	private ReservationStatus reservationStatus;
}
