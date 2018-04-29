package hotelreservation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;

@Service
public class UserService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private PrivilegeRepo privilegeRepo;

	public User createUser(User user, long userTypeId) {
		Role role = roleRepo.findOne(userTypeId);
		user.setRoles(Arrays.asList(role));
		return userRepo.save(user);
	}
	
	//TODO remove as the super admin user should be added by an sql script
	public User createUser(User user) {
//		UserType userType = userTypeRepo.findOne(userTypeId);
//		user.setUserType(userType);
		
		user.setCreatedOn(new Date());
		//TODO set created BY
		
		
		return userRepo.save(user);
	}
	
	public Role createRole(Role userType) {
		return roleRepo.save(userType);
	}

	public List<Role> getAllRoles() {
		Iterable<Role> findAll = roleRepo.findAll();

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
	
	public Role getRoleById(Optional<Integer> id) {
		return roleRepo.findOne(new Long(id.get()));
	}

	public void deleteUser(Long id) {
		userRepo.delete(id);
	}

	public void deleteRole(Long id) {
		roleRepo.delete(id);
	}

	public Privilege getPrivilegeByName(String name) {
		return privilegeRepo.findByName(name);
	}

	public void createPrivilege(Privilege privilege) {
		privilegeRepo.save(privilege);
		
	}

	public Role getRoleByName(String name) {
		return roleRepo.findByName(name);
	}
}