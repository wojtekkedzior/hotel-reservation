package hotelreservation.model;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import hotelreservation.model.enums.ReservationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.NoArgsConstructor;

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