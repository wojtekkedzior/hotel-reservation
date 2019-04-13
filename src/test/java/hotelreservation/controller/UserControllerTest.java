package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.RestExceptionHandler;
import hotelreservation.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest  {

	private MockMvc mvc;
	
	@Autowired
	private UsersController usersController;
	
	private User user;

	@Before
	public void setup() {
		this.mvc = standaloneSetup(usersController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
		user = new User();
		user.setPassword("password");
		user.setFirstName("user");
		user.setLastName("user");
		user.setUserName("user");
		user.setCreatedOn(new Date());
		user.setEnabled(true);
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isOk());
		mvc.perform(post("/adduser").flashAttr("user", user)).andExpect(status().is3xxRedirection());
		//is in error because of constraint violations
		mvc.perform(delete("/userDelete/1")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		//nothing here
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isOk());
		mvc.perform(post("/adduser").flashAttr("user", user)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/userDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		//nothing here
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isForbidden());
		mvc.perform(post("/adduser").flashAttr("user", user)).andExpect(status().isForbidden());
		mvc.perform(delete("/userDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isForbidden());
		mvc.perform(post("/adduser").flashAttr("user", user)).andExpect(status().isForbidden());
		mvc.perform(delete("/userDelete/1")).andExpect(status().isForbidden());
	}
}