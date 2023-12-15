package hotelreservation.model;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userName" }, name = "uq_userName"))
@NoArgsConstructor
@AllArgsConstructor
@Audited
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

	@OneToOne
	private User createdBy;

	@NotNull
	private LocalDateTime createdOn;

	@OneToOne
	private User disabledBy;

	private LocalDateTime disabledOn;

	private LocalDateTime lastloggedOn;

	@NotAudited
	@OneToOne(fetch = FetchType.EAGER)
	@NotNull
	private Role role;
}