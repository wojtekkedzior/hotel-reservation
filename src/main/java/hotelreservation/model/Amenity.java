package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Amenity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@NotBlank
	private String name;
	
	private String description;
	private boolean enabled;
	
	//TODO add @NotNull
	@ManyToOne
	private AmenityType amenityType;
	
	public Amenity() {}

	public Amenity(String name, String description, AmenityType amenityType) {
		this.name = name;
		this.description = description;
		this.amenityType = amenityType;
	}  
}
