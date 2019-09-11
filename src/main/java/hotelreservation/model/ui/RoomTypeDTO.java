package hotelreservation.model.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

@Data
@Immutable
@AllArgsConstructor
public final class RoomTypeDTO {

	private String name;

	private String description;

}
