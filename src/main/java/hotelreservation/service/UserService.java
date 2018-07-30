package hotelreservation.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

	//TODO remove as the super admin user should be added by an sql script
	public User saveUser(User user) {
		user.setCreatedOn(new Date());
		//TODO set created BY
//		user.setCreatedBy(createdBy);
		String password = user.getPassword();
		
		if(password == null || password.isEmpty()) {
			throw new MissingOrInvalidArgumentException("password can't be empty");
		}
			
		user.setPassword(passwordEncoder.encode(password));
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

	public User getUserById(Integer id) {
		log.info("Looking for User with ID: " + id);
		Optional<User> findById = userRepo.findById(new Long(id));
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
	}
	
	public Role getRoleById(Integer id) {
		log.info("Looking for Role with ID: " + id);
		Optional<Role> findById = roleRepo.findById(new Long(id));
		if(findById.isPresent()) {
			return findById.get();
		} else {
			throw new NotFoundException(id);
		}
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