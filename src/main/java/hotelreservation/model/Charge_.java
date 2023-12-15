package hotelreservation.model;

import hotelreservation.model.enums.Currency;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Charge.class)
public class Charge_ {
	
	public static volatile SingularAttribute<Charge, Long> id;
	public static volatile SingularAttribute<Charge, String> name;
	public static volatile SingularAttribute<Charge, String> description;
	public static volatile SingularAttribute<Charge, Currency> currency;
	public static volatile SingularAttribute<Charge, Integer> value;
}
