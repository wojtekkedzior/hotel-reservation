package hotelreservation.controller;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.ui.UserDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest  {

	private MockMvc mvc;
	
	@Autowired
	private UsersController usersController;
	
	private UserDTO userDTO;

	@Autowired
	private ApplicationStartup applicationStartup;

	@Before
	public void setup() {
		this.mvc = standaloneSetup(usersController).setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
		userDTO = new UserDTO();
		userDTO.setPassword("password");
		userDTO.setFirstName("user");
		userDTO.setLastName("user");
		userDTO.setUserName("user");
		userDTO.setRole(applicationStartup.admin.getRole());
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isOk());
		mvc.perform(post("/adduser").flashAttr("userDTO", userDTO)).andExpect(status().is3xxRedirection());
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
		mvc.perform(post("/adduser").flashAttr("userDTO", userDTO)).andExpect(status().is3xxRedirection());
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
		mvc.perform(post("/adduser").flashAttr("userDTO", userDTO)).andExpect(status().isForbidden());
		mvc.perform(delete("/userDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isForbidden());
		mvc.perform(post("/adduser").flashAttr("userDTO", userDTO)).andExpect(status().isForbidden());
		mvc.perform(delete("/userDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetUserWithNoId() throws Exception {
		mvc.perform(get("/user/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetUser() throws Exception {
		mvc.perform(get("/user/1")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteUserWithWrongId() throws Exception {
		mvc.perform(delete("/userDelete/99")).andExpect(status().is2xxSuccessful());
	}

    @Test
    @WithUserDetails("admin")
    public void testDeleteUserWithNoId() throws Exception {
        mvc.perform(delete("/userDelete/")).andExpect(status().is4xxClientError());
    }
}