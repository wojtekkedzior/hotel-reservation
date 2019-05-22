package hotelreservation.repository;


import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.User;

@Repository
public interface UserRepo extends CrudRepository<User, Long>{

	Optional<User> findByUserName(String userName);
}
