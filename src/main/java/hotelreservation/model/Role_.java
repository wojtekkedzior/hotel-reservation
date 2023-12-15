package hotelreservation.model;

import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Role.class)
public class Role_ {
	
	public static volatile SingularAttribute<Role, Long> id;
	public static volatile SingularAttribute<Role, String> name;
	public static volatile SingularAttribute<Role, String> description;
	public static volatile SingularAttribute<Role, Boolean> enabled;
	public static volatile CollectionAttribute<Role, RoomRate> privileges;
}
