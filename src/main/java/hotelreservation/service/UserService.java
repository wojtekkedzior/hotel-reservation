package hotelreservation.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

	public User saveUser(User user, String name) {
		if (utils.isNullOrEmpty(name)) {
			throw new MissingOrInvalidArgumentException("missing name");
		}
		
		if(userRepo.findByUserName(user.getUserName()).isPresent()) {
			throw new MissingOrInvalidArgumentException("Username already exists: " + user.getUserName());
		}

		User userByUserName = getUserByUserName(name);
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
		
		user.setCreatedOn(LocalDateTime.now());
		return userRepo.save(user);
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

	public User getUserById(Long userId) {
		log.info("Looking for User with ID: {}", userId);
		return userRepo.findById(userId).orElseThrow(() -> new NotFoundException(userId));
	}
	
	public Role getRoleById(Integer id) {
		log.info("Looking for Role with ID: {}", id);
		return roleRepo.findById(Long.valueOf(id)).orElseThrow(() -> new NotFoundException(id));
	}
	
	public User getUserByUserName(String userName) {
		return userRepo.findByUserName(userName).orElseThrow(() -> new NotFoundException(0));
	}

	public void deleteUser(Long userId) {
		if(!userRepo.existsById(userId)) {
			throw new NotDeletedException(userId);
		}
		userRepo.deleteById(userId);
	}

//	public void deleteRole(Role role) {
//		if(!roleRepo.existsById(role.getId())) {
//			throw new NotDeletedException(role.getId());
//		}
//
//		roleRepo.delete(role);
//	}

	public List<Privilege> getAllPrivileges() {
		return utils.toList(privilegeRepo.findAll());
	}

	public void deleteUserRole(Long id) {
		if(!roleRepo.existsById(id)) {
			throw new NotDeletedException(id);
		}

		roleRepo.deleteById(id);

	}
}