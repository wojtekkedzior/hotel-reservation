package hotelreservation.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import hotelreservation.model.enums.Currency;
import lombok.Data;

@Entity
@Data
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
	private Date startDate;
	private Date endDate;

	public RoomRate() {
	}

	public RoomRate(Room room, Currency currency, double value, Date startDate, Date endDate) {
		super();
		this.room = room;
		this.currency = currency;
		this.value = value;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
}