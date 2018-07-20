package hotelreservation.service;

import java.util.ArrayList;
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
	
	//TODO remove as the super admin user should be added by an sql script
	public User createUser(User user) {
		user.setCreatedOn(new Date());
		//TODO set created BY
//		user.setCreatedBy(createdBy);
		
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
		return userRepo.findById(new Long(id.get())).get();
	}
	
	public Role getRoleById(Optional<Integer> id) {
		return roleRepo.findById(new Long(id.get())).get();
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

	public void deleteUser(User user) {
		userRepo.delete(user);
	}

	public void deleteRole(Role role) {
		roleRepo.delete(role);
	}
}