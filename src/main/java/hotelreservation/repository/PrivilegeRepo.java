package hotelreservation.repository;

import hotelreservation.model.Privilege;
import org.springframework.data.repository.CrudRepository;

public interface PrivilegeRepo extends CrudRepository<Privilege, Long>{

	Privilege findByName(String name);

}
