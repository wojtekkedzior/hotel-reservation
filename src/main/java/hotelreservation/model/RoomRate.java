package hotelreservation.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import hotelreservation.model.enums.Currency;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"room_id" , "day"})})
public class RoomRate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	private long id;

	private String description;

	@ManyToOne
	private Room room;

	@Enumerated(EnumType.STRING)
	private Currency currency;

	private double value;
	
	private LocalDate day;

	public RoomRate() { 
	}

	public RoomRate(Room room, Currency currency, double value, LocalDate day) {
		super();
		this.room = room;
		this.currency = currency;
		this.value = value;
		this.day = day;
	}
	 
}
