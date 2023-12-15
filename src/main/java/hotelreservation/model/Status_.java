package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Status.class)
public class Status_ {

	public static volatile SingularAttribute<Status, Long> id;
	public static volatile SingularAttribute<Status, String> name;
	public static volatile SingularAttribute<Status, String> description;
}
