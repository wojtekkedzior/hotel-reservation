package hotelreservation.model;

import org.hibernate.envers.Audited;

import hotelreservation.model.enums.IdType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Audited
public class Identification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	private String name;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private IdType idType;
	
	@NotNull
	@NotBlank
	private String idNumber;
	
	public Identification(IdType idType, String idNumber) {
		this.idType = idType;
		this.idNumber = idNumber;
	}
}
