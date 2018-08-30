package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@NotBlank
	private String country;
	
	@NotNull
	@NotBlank
	private String address;
	
	public Contact() {}
	
	public Contact(String address, String country) {
		this.address = address;
		this.country = country;
	}

}
