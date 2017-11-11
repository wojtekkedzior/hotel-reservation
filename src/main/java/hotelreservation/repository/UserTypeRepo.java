package hotelreservation.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.UserType;

@Repository
public interface UserTypeRepo extends CrudRepository<UserType, Long>{
	
}
