package hotelreservation.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Privilege {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
    private String name;
 
//    @ManyToMany(mappedBy = "privileges")
//    private Collection<Role> roles;
    
    public Privilege() {}

	public Privilege(String name) {
		super();
		this.name = name;
	}
	
	
    
}
