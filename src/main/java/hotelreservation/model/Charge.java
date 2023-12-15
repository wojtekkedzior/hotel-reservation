package hotelreservation.model;

import org.hibernate.envers.Audited;

import hotelreservation.model.enums.Currency;
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
public class Charge {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;
	
	@NotNull
	@NotBlank
	private String name;
	
	private String description;
	
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@NotNull
	private int value;
	
	public Charge(Currency currency, int value, String name, String description) {
		this.currency = currency;
		this.value = value;
		this.name = name;
		this.description = description;
	}
	
	
}
