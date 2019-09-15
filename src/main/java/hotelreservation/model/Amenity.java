package hotelreservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Amenity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@NotBlank
	private String name;
	
	private String description;
	private boolean enabled;
	
	//TODO add @NotNull
	@ManyToOne
    @NotAudited
	private AmenityType amenityType;
	
	public Amenity(String name, String description, AmenityType amenityType) {
		this.name = name;
		this.description = description;
		this.amenityType = amenityType;
	}  
}
