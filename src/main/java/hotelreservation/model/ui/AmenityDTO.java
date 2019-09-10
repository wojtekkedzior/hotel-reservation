package hotelreservation.model.ui;

import hotelreservation.model.AmenityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

@Data
@Immutable
@AllArgsConstructor
public final class AmenityDTO {

	private String name;

	private String description;

	private AmenityType amenityType;
}
