package hotelservation.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hotelreservation.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest (classes = Application.class)
@DataJpaTest
@AutoConfigureMockMvc
public class ReservationControllerTest {
	
	  @Autowired
	    private MockMvc mockMvc;
	  
//	    @Autowired
//	    private WebApplicationContext context;
	  
//		@Before
//		public void setup() {
//			mockMvc = MockMvcBuilders
//	                .webAppContextSetup(context)
////	                .apply(springSecurity())
//	                .build();
//		}
	  
	   @Test
	    @WithMockUser(roles={"BOB"})
	    public void testAdminPathIsNotAvailableToViewer() throws Exception {
//	    	mockMvc.perform(get("/"))
//	    			.andExpect(status().isForbidden()); // Expect that VIEWER users are forbidden from accessing this page
	    }
	   
	    @Test
	    @WithMockUser(roles={"ADMIN"})
	    public void testTwo() throws Exception {
//	        userService.methodTwo("This is Admin");
	    	
//	        this.mockMvc.perform(get("/"))
//	        .andDo(print())
////	        .andExpect(status().isOk())  
//	        .andExpect(view().name("redirect:/reservationDashBoard"));  
	    	
	    	
//	    	mockMvc.perform(get("/reservationDashBoard"))
//			.andExpect(status().isForbidden()); // Expect that VIEWER users are forbidden from accessing this page
//	    	
	    	
	    	
//	        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/realiseReservation/{id}", Optional.of(1))
//	                // ADD this line
////	                .with(user("admin").roles("USER","ADMIN"))
//	        		
//
////	                .content(new ObjectMapper().writeValueAsString(passwordChange))
//	                .contentType(MediaType.ALL)
//	                .accept(MediaType.ALL))
//	                .andExpect(status().isOk());
	        
	    	//Forbidden
	        this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("admin").roles("MANAGER")))
	        .andDo(print())
	        .andExpect(status().isForbidden()); 
	        
	        
//	        this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN")))
//	        .andDo(print())
//	        .andExpect(status().isForbidden()); 
	        
	        
	    	
	        // Send password change request
//	        PasswordChangeRepresentation passwordChange = new PasswordChangeRepresentation(DefaultUsers.Admin.getPassword(), "12345678");
//	        mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/hotelreservation/realiseReservation")
//	                // ADD this line
//	                .with(user("admin").roles("USER","ADMIN"))
//
////	                .content(new ObjectMapper().writeValueAsString(passwordChange))
//	                .contentType(MediaType.ALL)
//	                .accept(MediaType.ALL))
//	                .andExpect(status().isOk());

	        // Check that the password has been changed
//	        User user = this.userRepository.findByUsername(DefaultUsers.Admin.getEmail());
//	        assertEquals(user.getPassword(), "12345678");
	    } 

}
