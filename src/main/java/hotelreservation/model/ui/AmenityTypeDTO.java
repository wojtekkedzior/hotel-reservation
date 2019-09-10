package hotelreservation.model.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

@Data
@Immutable
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public final class AmenityTypeDTO {

	private Long id;

	private String name;

	private String description;

	public AmenityTypeDTO(String name, String description) {
		this.name = name;
		this.description = description;
	}

}
