package hotelreservation.model;

import java.time.LocalDateTime;

import hotelreservation.model.enums.Currency;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RoomRate.class)
public class RoomRate_ {
	public static volatile SingularAttribute<RoomRate, Long> id;
	public static volatile SingularAttribute<RoomRate, String> description;
	
	public static volatile SingularAttribute<RoomRate, Room> room;
	public static volatile SingularAttribute<RoomRate, Currency> currency;
	public static volatile SingularAttribute<RoomRate, Integer> value;
	
	public static volatile SingularAttribute<RoomRate, LocalDateTime> day;
	 
	//TODO implement toString() and hash() and debug why in the addReseration.html the id has to be the value and the value be the id.  
	//The idea is that either toString() or hash() are not returning the correct object based on the value, which after all is very repetative.
	
	//TODO add active/inactive to disable a room rate
}

