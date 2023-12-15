package hotelreservation.model.ui;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Immutable;
import org.springframework.format.annotation.DateTimeFormat;

import hotelreservation.model.Guest;
import hotelreservation.model.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

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
