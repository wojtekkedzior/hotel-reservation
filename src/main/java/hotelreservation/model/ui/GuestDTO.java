package hotelreservation.model.ui;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.Contact;
import hotelreservation.model.Identification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Immutable
@AllArgsConstructor
public final class GuestDTO {

	@NotNull
	@NotBlank
	private String firstName;

	@NotNull
	@NotBlank
	private String lastName;

	private String description;

	@NotNull
	private Contact contact;

	@NotNull
	private Identification identification;
}
