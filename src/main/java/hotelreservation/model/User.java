package hotelreservation.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints=@UniqueConstraint(columnNames = {"userName"}, name = "uq_userName"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String userName;
	private String password;

	@OneToOne
	private UserType userType;
	
	private String name;
	private boolean enabled;
	
	@OneToOne
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@OneToOne
	private User disabledBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date disabledOn;
	
	@Temporal(TemporalType.TIMESTAMP) 
	private Date lastloggedOn;
	
	public User() {}

	public User(String usernName, UserType userType, String name) {
		this.userName = usernName;
		this.userType = userType;
		this.name = name;
		
		createdOn = new Date();
		enabled = true;
	}
	
	public User(String usernName, UserType userType, String name, User createdBy) {
		this.userName = usernName;
		this.userType = userType;
		this.name = name;
		this.createdBy = createdBy;
		
		createdOn = new Date();
		enabled = true;
	}

	
	
}
