package hotelreservation.model;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userName" }, name = "uq_userName"))
@NoArgsConstructor 
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

	//TODO a user must have a role
//	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles;

	public User(String userName, String firstName, String lastName, User createdBy) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.createdBy = createdBy;

		createdOn = LocalDateTime.now();
		enabled = true;
	}
}