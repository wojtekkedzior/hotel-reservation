package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AmenityType.class)
public class AmenityType_ {
	
	public static volatile SingularAttribute<AmenityType, Long> id;
	public static volatile SingularAttribute<AmenityType, String> name;
	public static volatile SingularAttribute<AmenityType, String> description;
}
