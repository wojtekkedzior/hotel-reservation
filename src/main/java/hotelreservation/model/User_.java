package hotelreservation.model;

import java.time.LocalDateTime;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, Long> id;
	public static volatile SingularAttribute<User, String> userName;
	public static volatile SingularAttribute<User, String> firstName;
	public static volatile SingularAttribute<User, String> lastName;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, Boolean> enabled;
	public static volatile SingularAttribute<User, User> createdBy;
	public static volatile SingularAttribute<User, LocalDateTime> createdOn;
	public static volatile SingularAttribute<User, User> disabledBy;
	
	public static volatile SingularAttribute<User, LocalDateTime> disabledOn;
	public static volatile SingularAttribute<User, LocalDateTime> lastloggedOn;
	public static volatile SingularAttribute<User, Role> role;
}