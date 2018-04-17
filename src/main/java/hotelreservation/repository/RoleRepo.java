package hotelreservation.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Role;

@Repository
public interface RoleRepo extends CrudRepository<Role, Long>{

	Role findByName(String name);
	
}
