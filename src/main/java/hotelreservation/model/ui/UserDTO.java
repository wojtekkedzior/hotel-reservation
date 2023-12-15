package hotelreservation.model.ui;

import org.springframework.data.annotation.Immutable;

import hotelreservation.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Immutable
public final class UserDTO {

	@NotNull
	@NotBlank
	private String firstName;

	@NotNull
	@NotBlank
	private String lastName;

	@NotNull
	@NotBlank
	private String userName;

	@NotNull
	@NotBlank
	private String password;
	
	@NotNull
	private Role role;

}
