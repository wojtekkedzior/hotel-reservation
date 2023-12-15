package hotelreservation.model.ui;

import java.time.LocalDate;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.Room;
import hotelreservation.model.enums.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Immutable
@AllArgsConstructor
public final class RoomRateDTO {

	private String description;

	@NotNull
	private Room room;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@NotNull
	private int value;

	@NotNull
	private LocalDate day;
}
