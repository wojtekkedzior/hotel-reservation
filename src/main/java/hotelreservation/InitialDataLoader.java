package hotelreservation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.UserService;

@Component
@Profile("dev")
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	boolean alreadySetup = false;

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private PrivilegeRepo privilegeRepo;
	
	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User admin;
	private User manager;
	private User receptionist;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
//		addPrivileges();
		
		

//		if (alreadySetup)
//			return;
//		Privilege readPrivilege = createPrivilegeIfNotFound("READ");
//		Privilege writePrivilege = createPrivilegeIfNotFound("createReservation");
//
//		List<Privilege> adminPrivileges = Arrays.asList(readPrivilege);
//		createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
//		
////		createRoleIfNotFound("ROLE_MANAGER", adminPrivileges);
////		createRoleIfNotFound("ROLE_RECEPTIONIST", Arrays.asList(readPrivilege));
//
//		Role adminRole = roleRepo.findByName("ROLE_ADMIN");
//		User user = new User();
//		user.setFirstName("Test");
//		user.setLastName("Test");
//		user.setUserName("test");
//		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
//		user.setPassword("test");
//		user.setRoles(Arrays.asList(adminRole));
//		user.setEnabled(true);
//		userRepository.save(user);
//
//		alreadySetup = true;
	}
	
	
	private void addPrivileges() {
		Privilege createAmenity = new Privilege("createAmenity");
		Privilege createAmenityType = new Privilege("createAmenityType");
		Privilege createRoom = new Privilege("createRoom");
		Privilege createRoomType = new Privilege("createRoomType");
		Privilege createRoomRate = new Privilege("createRoomRate");
		Privilege deleteAmenity = new Privilege("deleteAmenity");
		Privilege deleteAmenityType = new Privilege("deleteAmenityType");
		Privilege deleteRoom = new Privilege("deleteRoom");
		Privilege deleteRoomType = new Privilege("deleteRoomType");
		Privilege deleteRoomRate = new Privilege("deleteRoomRate");
		Privilege getReservation = new Privilege("getReservation");
		Privilege createReservation = new Privilege("createReservation");
		Privilege cancelReservation = new Privilege("cancelReservation");
		Privilege realiseReservation = new Privilege("realiseReservation");
		Privilege checkoutReservation = new Privilege("checkoutReservation");
		Privilege deleteReservation = new Privilege("deleteReservation");
		Privilege viewAdmin = new Privilege("viewAdmin");
		
		userService.createPrivilege(createAmenity);
		userService.createPrivilege(createAmenityType);
		userService.createPrivilege(createRoom);
		userService.createPrivilege(createRoomType);
		userService.createPrivilege(createRoomRate);
		userService.createPrivilege(deleteAmenity);
		userService.createPrivilege(deleteAmenityType);
		userService.createPrivilege(deleteRoom);
		userService.createPrivilege(deleteRoomType);
		userService.createPrivilege(deleteRoomRate);
		userService.createPrivilege(getReservation);
		userService.createPrivilege(createReservation);
		userService.createPrivilege(cancelReservation);
		userService.createPrivilege(realiseReservation);
		userService.createPrivilege(checkoutReservation);
		userService.createPrivilege(deleteReservation);
		userService.createPrivilege(viewAdmin);

		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();

		adminPrivileges.add(deleteRoom);
		adminPrivileges.add(deleteRoomType);
		adminPrivileges.add(deleteAmenity);
		adminPrivileges.add(deleteRoomRate);
		adminPrivileges.add(deleteAmenityType);
		adminPrivileges.add(createAmenity);
		adminPrivileges.add(createAmenityType);
		adminPrivileges.add(createRoom);
		adminPrivileges.add(createRoomType);
		adminPrivileges.add(deleteReservation);
		adminPrivileges.add(viewAdmin);

		
		managerPrivileges.add(getReservation);
		managerPrivileges.add(createReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(realiseReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(checkoutReservation);
		managerPrivileges.add(viewAdmin);
		managerPrivileges.add(createRoomRate);

		
		receptionistPrivileges.add(getReservation);
		receptionistPrivileges.add(createReservation);
		receptionistPrivileges.add(cancelReservation);
		receptionistPrivileges.add(realiseReservation);
		receptionistPrivileges.add(cancelReservation);
		receptionistPrivileges.add(checkoutReservation);
		
		
		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		receptionistRole = new Role("receptionist", "receptionist", true);

		adminRole.setPrivileges(adminPrivileges);
		managerRole.setPrivileges(managerPrivileges);
		receptionistRole.setPrivileges(receptionistPrivileges);

		userService.createRole(adminRole);
		userService.createRole(managerRole);
		userService.createRole(receptionistRole);

		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRoles(Arrays.asList(managerRole));
		userService.createUser(manager);

		admin = new User();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setUserName("admin");
		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		userService.createUser(admin);

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
		receptionist.setPassword("password");
		receptionist.setRoles(Arrays.asList(receptionistRole));
		receptionist.setEnabled(true);
		userService.createUser(receptionist);
	}
	
	
	//TODO make this work as now it breaks passwords
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	    return new BCryptPasswordEncoder();
//	}

//	@Transactional
//	private Privilege createPrivilegeIfNotFound(String name) {
//
//		Privilege privilege = privilegeRepo.findByName(name);
//		if (privilege == null) {
//			privilege = new Privilege(name);
//			privilegeRepo.save(privilege);
//		}
//		return privilege;
//	}
//
//	@Transactional
//	private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
//
//		Role role = roleRepo.findByName(name);
//		if (role == null) {
//			role = new Role(name, "ada", true);
//			role.setPrivileges(privileges);
//			roleRepo.save(role);
//		}
//		return role;
//	}
}