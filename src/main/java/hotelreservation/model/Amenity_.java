package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Amenity.class)
public class Amenity_ {
	
	public static volatile SingularAttribute<Amenity, Long> id;
	public static volatile SingularAttribute<Amenity, String> name;
	public static volatile SingularAttribute<Amenity, String> description;
	public static volatile SingularAttribute<Amenity, Boolean> enabled;
	public static volatile SingularAttribute<Amenity, AmenityType> amenityType;
}
