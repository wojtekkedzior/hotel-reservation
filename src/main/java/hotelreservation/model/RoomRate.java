package hotelreservation.model;

import java.time.LocalDate;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import hotelreservation.model.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

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

