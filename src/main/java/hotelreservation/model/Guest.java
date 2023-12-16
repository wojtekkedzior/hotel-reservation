package hotelreservation.model;

import org.hibernate.envers.Audited;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Builder
public class Guest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@NotBlank
	private String firstName;
	
	@NotNull
	@NotBlank
	private String lastName;
	
	private String description;
	
	@ManyToOne
	@NotNull
	private Contact contact;
	
	@ManyToOne
	@NotNull
	private Identification identification;
	
	public Guest(String firstName, String lastName, Contact contact, Identification identification) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.contact = contact;
		this.identification = identification;
	}
}
