package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class AmmenityType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

}
