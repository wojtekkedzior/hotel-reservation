package hotelreservation.model.ui;

import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.validation.constraints.NotNull;

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
