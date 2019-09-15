package hotelreservation.model;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@Audited
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@NotBlank
	private String country;
	
	@NotNull
	@NotBlank
	private String address;
	
	public Contact(String address, String country) {
		this.address = address;
		this.country = country;
	}

}
