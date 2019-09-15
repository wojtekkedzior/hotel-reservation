package hotelreservation.model;

import hotelreservation.model.enums.Currency;
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
