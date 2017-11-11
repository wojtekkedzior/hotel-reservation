package hotelreservation.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;


	private String usernName;
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

	
	
}
