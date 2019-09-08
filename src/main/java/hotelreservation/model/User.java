package hotelreservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userName" }, name = "uq_userName"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;

	@NotNull
	@NotBlank
	private String userName;

	@NotNull
	@NotBlank
	private String firstName;
	
	@NotNull
	@NotBlank
	private String lastName;

	@NotNull
	@NotBlank
	private String password;

	private boolean enabled;

	//TODO this will be funky for a super admin
//	@NotNull
	@OneToOne
	private User createdBy;

	@NotNull
	private LocalDateTime createdOn;

	@OneToOne
	private User disabledBy;

	private LocalDateTime disabledOn;

	private LocalDateTime lastloggedOn;

//	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private Role role;
}