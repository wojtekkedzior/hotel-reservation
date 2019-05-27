package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import hotelreservation.model.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor 
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
