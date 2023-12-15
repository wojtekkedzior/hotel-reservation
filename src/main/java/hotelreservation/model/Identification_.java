package hotelreservation.model;

import hotelreservation.model.enums.IdType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Identification.class)
public class Identification_ {
	
	public static volatile SingularAttribute<Identification, Long> id;
	public static volatile SingularAttribute<Identification, String> name;
	public static volatile SingularAttribute<Identification, String> description;
	public static volatile SingularAttribute<Identification, IdType> idType;
	public static volatile SingularAttribute<Identification, String> idNumber;
}
