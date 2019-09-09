package hotelreservation.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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
