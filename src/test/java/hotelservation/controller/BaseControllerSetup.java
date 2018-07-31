package hotelservation.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.UserService;

public abstract class BaseControllerSetup {
	
	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User admin;
	private User manager;
	private User receptionist;
	protected User superAdmin;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PlatformTransactionManager txManager;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// Nasty hack - using @WithUserDetails causes the UserDetails service to be called as part of the securtiy chain, which happens before the @Before.
	// Hopefully this will be fixed in some never version
	@BeforeTransaction
	public void setupExtra() {
		new TransactionTemplate(txManager).execute(status -> {
			createAdminUser();
			getPrivilegesForReceptionist();
			getPrivilegesForManager();
			getPrivilegesForAdmin();
			
			addPrivileges();
			return null;
		});
	}

	abstract Collection<Privilege> getPrivilegesForReceptionist();
	abstract Collection<Privilege> getPrivilegesForManager();
	abstract Collection<Privilege> getPrivilegesForAdmin();
	
	public void createAdminUser() {
		superAdmin = new User();
		superAdmin.setUserName("superAdmin");
		superAdmin.setCreatedOn(new Date());
		superAdmin.setPassword(passwordEncoder.encode("superAdminPassword"));
		userRepo.save(superAdmin);
	}

	private void addPrivileges() {
		Collection<Privilege> adminPrivileges = getPrivilegesForAdmin();
		Collection<Privilege> managerPrivileges = getPrivilegesForManager();
		Collection<Privilege> receptionistPrivileges = getPrivilegesForReceptionist();
		
		for (Privilege privilege : adminPrivileges) {
			userService.savePrivilege(privilege);
		}
		
		for (Privilege privilege : managerPrivileges) {
			userService.savePrivilege(privilege);
		}
		
		for (Privilege privilege : receptionistPrivileges) {
			userService.savePrivilege(privilege);
		}

		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		receptionistRole = new Role("receptionist", "receptionist", true);

		adminRole.setPrivileges(adminPrivileges);
		managerRole.setPrivileges(managerPrivileges);
		receptionistRole.setPrivileges(receptionistPrivileges);

		userService.saveRole(adminRole);
		userService.saveRole(managerRole);
		userService.saveRole(receptionistRole);

		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRoles(Arrays.asList(managerRole));
		userService.saveUser(manager, superAdmin.getUserName());

		admin = new User();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setUserName("admin");
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		userService.saveUser(admin, superAdmin.getUserName());

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		receptionist.setPassword("password");
		receptionist.setRoles(Arrays.asList(receptionistRole));
		receptionist.setEnabled(true);
		userService.saveUser(receptionist, superAdmin.getUserName());
	}
}