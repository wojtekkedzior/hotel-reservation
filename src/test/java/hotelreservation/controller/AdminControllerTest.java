package hotelreservation.controller;

import 
static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hotelreservation.Application;
import hotelreservation.WebSecurityConfig;
import hotelreservation.model.Privilege;
import hotelreservation.service.MyUserDetailsService;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { MyUserDetailsService.class })
//@DataJpaTest
//@AutoConfigureMockMvc
//@SpringBootTest
//@WebMvcTest

//@ContextConfiguration
@WebAppConfiguration

//@Import(MyUserDetailsService.class)
//@WebMvcTest(includeFilters = @Filter(classes = EnableWebSecurity.class))
//@Import(WebSecurityConfig.class)
@ContextConfiguration(classes = Application.class)
//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AdminControllerTest extends BaseControllerSetup {

//	@Autowired
//	private MockMvc mvc;

//	@Before
//	public void setup() {
////		 mvc = MockMvcBuilders
////	                .webAppContextSetup(context)
////	                .apply(springSecurity()) // enable security for the mock set up
////	                .build();
//	}
	
	  @Autowired
	    private WebApplicationContext context;

	    private MockMvc mvc;

	    @Before
	    public void setup() {
	        mvc = MockMvcBuilders
	                .webAppContextSetup(context)
	                .apply(SecurityMockMvcConfigurers.springSecurity())
	                .build();
	    }

	    
	    
	
//    @Autowired
//    private WebApplicationContext context;


	@Override
	Collection<Privilege> getPrivilegesForReceptionist() {
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();
		receptionistPrivileges.add(new Privilege("getReservation"));
		receptionistPrivileges.add(new Privilege("createReservation"));
		receptionistPrivileges.add(new Privilege("cancelReservation"));
		receptionistPrivileges.add(new Privilege("realiseReservation"));
		receptionistPrivileges.add(new Privilege("checkoutReservation"));
		return receptionistPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForManager() {
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		managerPrivileges.add(new Privilege("getReservation"));
		managerPrivileges.add(new Privilege("createReservation"));
		managerPrivileges.add(new Privilege("cancelReservation"));
		managerPrivileges.add(new Privilege("realiseReservation"));
		managerPrivileges.add(new Privilege("checkoutReservation"));
		managerPrivileges.add(new Privilege("viewAdmin"));
		return managerPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForAdmin() {
		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		adminPrivileges.add(new Privilege("deleteReservation"));
		adminPrivileges.add(new Privilege("viewAdmin"));
		return adminPrivileges;
	}
	
	@Test
	@WithUserDetails("manager")
	public void testAdminCanAccessAdmin() throws Exception {
		mvc.perform(get("/admin")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("manager")
//	 @WithMockUser(value = "test", password = "pass")
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