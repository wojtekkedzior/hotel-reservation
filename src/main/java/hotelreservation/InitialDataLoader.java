package hotelreservation;

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

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (alreadySetup)
			return;
		Privilege readPrivilege = createPrivilegeIfNotFound("READ");
		Privilege writePrivilege = createPrivilegeIfNotFound("createReservation");

		List<Privilege> adminPrivileges = Arrays.asList(readPrivilege);
		createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
		
//		createRoleIfNotFound("ROLE_MANAGER", adminPrivileges);
//		createRoleIfNotFound("ROLE_RECEPTIONIST", Arrays.asList(readPrivilege));

		Role adminRole = roleRepo.findByName("ROLE_ADMIN");
		User user = new User();
		user.setFirstName("Test");
		user.setLastName("Test");
		user.setUserName("test");
		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
		user.setPassword("test");
		user.setRoles(Arrays.asList(adminRole));
		user.setEnabled(true);
		userRepository.save(user);

		alreadySetup = true;
	}
	
	//TODO make this work as now it breaks passwords
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	    return new BCryptPasswordEncoder();
//	}

	@Transactional
	private Privilege createPrivilegeIfNotFound(String name) {

		Privilege privilege = privilegeRepo.findByName(name);
		if (privilege == null) {
			privilege = new Privilege(name);
			privilegeRepo.save(privilege);
		}
		return privilege;
	}

	@Transactional
	private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

		Role role = roleRepo.findByName(name);
		if (role == null) {
			role = new Role(name, "ada", true);
			role.setPrivileges(privileges);
			roleRepo.save(role);
		}
		return role;
	}
}