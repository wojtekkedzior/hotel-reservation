package hotelreservation.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import hotelreservation.model.enums.Currency;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints=@UniqueConstraint(columnNames = {"room_id" , "day"}, name = "uq_name"))
public class RoomRate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String description;

	@NotNull
	@ManyToOne
	private Room room;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;

	@NotNull
	private int value;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date day;
	
	public RoomRate() { 
	}

	public RoomRate(Room room, Currency currency, int value, Date day) {
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
