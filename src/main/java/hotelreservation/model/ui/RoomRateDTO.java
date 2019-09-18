package hotelreservation.model.ui;

import hotelreservation.model.Room;
import hotelreservation.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
