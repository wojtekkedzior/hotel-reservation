package hotelreservation.model.ui;

import hotelreservation.model.Guest;
import hotelreservation.model.User;
import hotelreservation.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

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

	private double discount;

	private User discountAuthorisedBy;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	private ReservationStatus reservationStatus;
}
