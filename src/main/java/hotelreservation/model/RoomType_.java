package hotelreservation.model;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RoomType.class)
public class RoomType_ {
	public static volatile SingularAttribute<RoomType, Long> id;
	public static volatile SingularAttribute<RoomType, String> name;
	public static volatile SingularAttribute<RoomType, String> description;
}