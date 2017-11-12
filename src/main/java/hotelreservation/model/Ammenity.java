package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Ammenity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	private String description;
	private boolean enabled;
	
	@OneToOne
	private AmmenityType ammenityType;
	
	public Ammenity() {}

	public Ammenity(String name, String description, AmmenityType ammenityType) {
		this.name = name;
		this.description = description;
		this.ammenityType = ammenityType;
	} 
}
