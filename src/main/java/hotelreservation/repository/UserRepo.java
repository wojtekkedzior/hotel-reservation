package hotelreservation.repository;


import hotelreservation.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long>{

	Optional<User> findByUserName(String userName);
}
