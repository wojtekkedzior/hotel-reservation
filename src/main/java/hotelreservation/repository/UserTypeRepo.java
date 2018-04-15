package hotelreservation.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Role;

@Repository
public interface UserTypeRepo extends CrudRepository<Role, Long>{
	
}
