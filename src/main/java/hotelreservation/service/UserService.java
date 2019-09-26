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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final PrivilegeRepo privilegeRepo;
	private final Utils utils;
	private final PasswordEncoder passwordEncoder;

	public User saveUser(User user, String createdByName) {
		if (utils.isNullOrEmpty(createdByName)) {
			throw new MissingOrInvalidArgumentException("missing createdByName");
		}
		
		if(userRepo.findByUserName(user.getUserName()).isPresent()) {
			throw new MissingOrInvalidArgumentException("Username already exists: " + user.getUserName());
		}

		User createdByUser = getUserByUserName(createdByName);

		if(user.getRole() == null) {
			throw new MissingOrInvalidArgumentException("No role for given user");
		}

		if(createdByUser.getRole().getName().equals("admin")) {
			if(!user.getRole().getName().equals("manager") && !user.getRole().getName().equals("admin")) {
				throw new MissingOrInvalidArgumentException("Create by user: " + createdByUser.getId() + " as an admin can only create manager or admin roles, but tried to create: " + user.getRole());
			}
		} else if(createdByUser.getRole().getName().equals("manager")  && !user.getRole().getName().equals("receptionist")) { //A manager role can only create receptionists
				throw new MissingOrInvalidArgumentException("Create by user: " + createdByUser.getId() + " as a manager can only create receptionist roles, but tried to create: " + user.getRole());
		}

		user.setCreatedBy(createdByUser);

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
	
	public Role getRoleById(Long id) {
		log.info("Looking for Role with ID: {}", id);
		return roleRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
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