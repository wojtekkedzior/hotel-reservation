package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Privilege.class)
public class Privilege_ {
	public static volatile SingularAttribute<Privilege, Long> id;
	public static volatile SingularAttribute<Privilege, String> name;
}
