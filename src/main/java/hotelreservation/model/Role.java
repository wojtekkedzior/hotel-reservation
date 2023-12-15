package hotelreservation.model;

import java.util.Collection;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Audited
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;

	@NotNull
	@NotBlank
	private String name;
	
	private String description;
	private boolean enabled;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<Privilege> privileges;

	public Role(String name, String description, boolean enabled) {
		this.name = name;
		this.description = description;
		this.enabled = enabled;
	}

}
