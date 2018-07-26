package hotelservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hotelreservation.Application;
import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.service.MyUserDetailsService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AdminControllerTest {

	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User admin;
	private User manager;
	private User receptionist;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserService userService;

	@Autowired
	private PlatformTransactionManager txManager;

	// Nasty hack - using @WithUserDetails causes the UserDetails service to be called as part of the securtiy chain, which happens before the @Before.
	// Hopefully this will be fixed in some never version
	@BeforeTransaction
	public void setup1() {
		new TransactionTemplate(txManager).execute(status -> {
			addPrivileges();
			return null;
		});
	}

	@Before
	public void setup() {
	}

	private void addPrivileges() {
		Privilege getReservation = new Privilege("getReservation");
		Privilege createReservation = new Privilege("createReservation");

		Privilege cancelReservation = new Privilege("cancelReservation");
		Privilege realiseReservation = new Privilege("realiseReservation");
		Privilege checkoutReservation = new Privilege("checkoutReservation");

		Privilege deleteReservation = new Privilege("deleteReservation");
		Privilege viewAdmin = new Privilege("viewAdmin");

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

		adminPrivileges.add(deleteReservation);
		adminPrivileges.add(viewAdmin);

		managerPrivileges.add(getReservation);
		managerPrivileges.add(createReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(realiseReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(checkoutReservation);
		managerPrivileges.add(viewAdmin);

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
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		userService.createUser(admin);

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		receptionist.setPassword("password");
		receptionist.setRoles(Arrays.asList(receptionistRole));
		receptionist.setEnabled(true);
		userService.createUser(receptionist);
	}

	@Test
	@WithUserDetails("manager")
	public void testAdminCanAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testManagerCanAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testReceptionistForbiddenToAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username="nonExistentUser", roles = "receptionist")
	public void testInvalidUserIsForbidden() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testLogout() throws Exception {
		mvc.perform(post("/logout")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
	}

}