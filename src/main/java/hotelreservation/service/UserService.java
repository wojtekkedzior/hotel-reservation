package hotelreservation.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;

@Service
@Transactional
public class UserService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private PrivilegeRepo privilegeRepo;
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void saveUser(User user, String name) {
		if (utils.isNullOrEmpty(name)) {
			throw new MissingOrInvalidArgumentException("missing name");
		}

		User userByUserName = userRepo.findByUserName(name);
		if (userByUserName == null) {
			throw new MissingOrInvalidArgumentException("missing name");
		}

		// TODO ensure that the 'create by' user is actually allowed to create a user of the selected Type
		user.setCreatedBy(userByUserName);

		String password = user.getPassword();
		
		if(utils.isNullOrEmpty(password)) {
			throw new MissingOrInvalidArgumentException("password can't be empty");
		}
		
		user.setPassword(passwordEncoder.encode(password));
		
		if(utils.isNullOrEmpty(user.getUserName())) {
			throw new MissingOrInvalidArgumentException("Username can't be empty");
		}
		
		if(userRepo.findByUserName(user.getUserName()) != null) {
			throw new MissingOrInvalidArgumentException("Username already exists: " + user.getUserName());
		}
		
		user.setCreatedOn(new Date());
		userRepo.save(user);
	}
	
	public Role saveRole(Role userType) {
		return roleRepo.save(userType);
	}
	
	public void savePrivilege(Privilege privilege) {
		privilegeRepo.save(privilege);
	}
	
	public List<Role> getAllRoles() {
		return utils.toList(roleRepo.findAll());
	}

	public List<User> getAllUsers() {
		return utils.toList(userRepo.findAll());
	}

	public User getUserById(Integer id) {
		log.info("Looking for User with ID: " + id);
		return userRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}
	
	public Role getRoleById(Integer id) {
		log.info("Looking for Role with ID: " + id);
		return roleRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}

	public void deleteUser(User user) {
		if(!userRepo.existsById(user.getId())) {
			throw new NotDeletedException(user.getId());
		}
		
		userRepo.delete(user);
	}

	public void deleteRole(Role role) {
		if(!roleRepo.existsById(role.getId())) {
			throw new NotDeletedException(role.getId());
		}
		
		roleRepo.delete(role);
	}

	public List<Privilege> getAllPrivileges() {
		return utils.toList(privilegeRepo.findAll());
	}
}