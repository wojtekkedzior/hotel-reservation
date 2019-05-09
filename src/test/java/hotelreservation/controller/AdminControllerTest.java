package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.RestExceptionHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")

// If I want a fully isolated test env I should use WebMvcTest as it does not configure and DB stuff, but then I have to use MockBean and WithMockuser (maybe I can provide a
// differnt UserDatailsBean with an in memory setup), but being able to write tests against the same app setup i use for manual testing/playing around out weights the beneficts of
// mocks
// @WebMvcTest(value=AdminController.class)
// ContextConfig lets you build the context with only the classes and mocks needed for this controller test. otherwise you need to satisfy all dependencies in other controllers
// using mocks
// @ContextConfiguration(classes= {MyUserDetailsService.class, WebSecurityConfig.class, HotelReservationAccessDeniedHandler.class, AdminController.class})

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AdminControllerTest {
	
	private MockMvc mvc;

	@Autowired
	private AdminController adminController;
	
	@Before
	public void setup() {
		this.mvc = standaloneSetup(adminController).setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminCanAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerCanAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistForbiddenToAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isForbidden());
	}

	//Note to self - have to use WithMockUser here because if you specify a user that is not in the setup data you will never even get to this test.
	@Test
	@WithMockUser("nonExistentUser")
	public void testInvalidUserIsForbidden() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testLogout() throws Exception {
		mvc.perform(post("/logout")).andExpect(status().is4xxClientError()).andExpect(redirectedUrl(null));
	}
}