package hotelreservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	
	@OneToOne
	@NotNull
	private Contact contact;
	
	@OneToOne
	@NotNull
	private Identification identification;
	
	public Guest(String firstName, String lastName, Contact contact, Identification identification) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.contact = contact;
		this.identification = identification;
	}
}
