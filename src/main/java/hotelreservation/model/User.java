package hotelreservation.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userName" }, name = "uq_userName"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@OneToOne
	private User disabledBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date disabledOn;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastloggedOn;

	@ManyToMany
	private Collection<Role> roles;

	public User() {
	}

/*	public User(String usernName) {
		this.userName = usernName;

		createdOn = new Date();
		enabled = true;
	}*/

	public User(String usernName, String firstName, String lastName, User createdBy) {
		this.userName = usernName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.createdBy = createdBy;

		createdOn = new Date();
		enabled = true;
	}
}
