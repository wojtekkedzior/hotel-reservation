package hotelreservation.model.ui;

import hotelreservation.model.Contact;
import hotelreservation.model.Identification;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
