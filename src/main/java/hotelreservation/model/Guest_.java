package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Guest.class)
public class Guest_ {
	
	public static volatile SingularAttribute<Guest, Long> id;
	public static volatile SingularAttribute<Guest, String> firstName;
	public static volatile SingularAttribute<Guest, String> lastName;
	public static volatile SingularAttribute<Guest, String> description;
	public static volatile SingularAttribute<Guest, Contact> contact;
	public static volatile SingularAttribute<Guest, Identification> identification;
}
