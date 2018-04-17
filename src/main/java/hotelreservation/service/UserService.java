package hotelreservation.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.User;
import hotelreservation.model.Role;
import hotelreservation.repository.UserRepo;
import hotelreservation.repository.RoleRepo;

@Service
public class UserService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo userTypeRepo;

	public User createUser(User user, long userTypeId) {
		Role userType = userTypeRepo.findOne(userTypeId);
		user.setUserType(userType);
		return userRepo.save(user);
	}
	
	//TODO remove as the super admin user should be added by an sql script
	public User createUser(User user) {
//		UserType userType = userTypeRepo.findOne(userTypeId);
//		user.setUserType(userType);
		
		user.setCreatedOn(new Date());
		return userRepo.save(user);
	}
	
	public Role createUserType(Role userType) {
		return userTypeRepo.save(userType);
	}

	public List<Role> getAllUserTypes() {
		Iterable<Role> findAll = userTypeRepo.findAll();

		List<Role> target = new ArrayList<Role>();
		findAll.forEach(target::add);

		return target;
	}

	public List<User> getAllUsers() {
		Iterable<User> findAll = userRepo.findAll();

		List<User> target = new ArrayList<User>();
		findAll.forEach(target::add);

		return target;
	}

	public User getUserById(Optional<Integer> id) {
		return userRepo.findOne(new Long(id.get()));
	}
	
	public Role getUserTypeById(Optional<Integer> id) {
		return userTypeRepo.findOne(new Long(id.get()));
	}

	public void deleteUser(Long id) {
		userRepo.delete(id);
	}

	public void deleteUserType(Long id) {
		userTypeRepo.delete(id);
	}
}
