package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Guest {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String firstName;
	private String lastName;
	private String description;
	
	@OneToOne
	private Contact contact;
	
	@OneToOne
	private Identification identification;
	
	public Guest() {}

	public Guest(String firstName, String lastName, String description, Contact contact, Identification identification) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
		this.contact = contact;
		this.identification = identification;
	}
	
	

}
