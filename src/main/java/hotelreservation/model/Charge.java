package hotelreservation.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import hotelreservation.model.enums.Currency;
import lombok.Data;

@Entity
@Data
public class Charge {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	private int value;
	
	public Charge() {}

	public Charge(Currency currency, int value) {
		this.currency = currency;
		this.value = value;
	}
	
	
}
