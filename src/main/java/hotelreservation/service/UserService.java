package hotelreservation.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.repository.UserRepo;
import hotelreservation.repository.UserTypeRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserTypeRepo userTypeRepo;

	public User createUser(User user, long userTypeId) {
		UserType userType = userTypeRepo.findById(userTypeId).get();
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
	
	public UserType createUserType(UserType userType) {
		return userTypeRepo.save(userType);
	}

	public List<UserType> getAllUserTypes() {
		Iterable<UserType> findAll = userTypeRepo.findAll();

		List<UserType> target = new ArrayList<UserType>();
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
		return userRepo.findById(new Long(id.get())).get();
	}
	
	public UserType getUserTypeById(Optional<Integer> id) {
		return userTypeRepo.findById(new Long(id.get())).get();
	}

	public void deleteUser(Long id) {
		userRepo.deleteById(id);
	}

	public void deleteUserType(Long id) {
		userTypeRepo.deleteById(id);
	}
}
