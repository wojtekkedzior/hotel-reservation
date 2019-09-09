package hotelreservation.model.ui;

import hotelreservation.model.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Immutable
@RequiredArgsConstructor
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
