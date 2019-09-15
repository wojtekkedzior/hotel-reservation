package hotelreservation.model;

import hotelreservation.model.enums.IdType;
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
