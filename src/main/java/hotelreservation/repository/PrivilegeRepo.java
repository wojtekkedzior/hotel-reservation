package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Privilege;

public interface PrivilegeRepo extends CrudRepository<Privilege, Long>{

	Privilege findByName(String name);

}
