package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import hotelreservation.model.enums.IdType;
import lombok.Data;

@Entity
@Data
public class Identification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String name;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private IdType idType;
	
	private String idNumber;
	
	
	public Identification() {}

	public Identification(String name, String description, IdType idType) {
		super();
		this.name = name;
		this.description = description;
		this.idType = idType;
	}

}
