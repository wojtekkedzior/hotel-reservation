package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import hotelreservation.model.enums.Currency;
import lombok.Data;

@Entity
@Data
public class Charge {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@NotBlank
	private String name;
	
	private String description;
	
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@NotNull
	private int value;
	
	public Charge() {}

	public Charge(Currency currency, int value, String name, String description) {
		this.currency = currency;
		this.value = value;
		this.name = name;
		this.description = description;
	}
	
	
}
