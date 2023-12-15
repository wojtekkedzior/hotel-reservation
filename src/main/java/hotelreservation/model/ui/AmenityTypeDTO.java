package hotelreservation.model.ui;

import org.springframework.data.annotation.Immutable;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Immutable
public final class AmenityTypeDTO {

	@NotNull
	private String name;

	private String description;

	public AmenityTypeDTO(String name, String description) {
		this.name = name;
		this.description = description;
	}

}
