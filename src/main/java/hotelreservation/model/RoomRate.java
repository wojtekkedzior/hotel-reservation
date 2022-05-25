package hotelreservation.model;

import hotelreservation.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Table(uniqueConstraints=@UniqueConstraint(columnNames = {"room_id" , "day"}, name = "uq_name"))
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Builder
public class RoomRate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Exclude
	private long id;

	private String description;

	@NotNull
	@ManyToOne
	@NotAudited
	private Room room;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@NotNull
	private int value;
	
	@NotNull
	private LocalDate day;
	
	public RoomRate(Room room, Currency currency, int value, LocalDate day) {
		super();
		this.room = room;
		this.currency = currency;
		this.value = value;
		this.day = day;
	}
	 
	//TODO implement toString() and hash() and debug why in the addReseration.html the id has to be the value and the value be the id.  
	//The idea is that either toString() or hash() are not returning the correct object based on the value, which after all is very repetative.
	
	//TODO add active/inactive to disable a room rate
}

