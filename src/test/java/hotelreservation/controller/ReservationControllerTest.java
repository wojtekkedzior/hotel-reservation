package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.service.BookingService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationControllerTest {

	private MockMvc mvc;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private ApplicationStartup applicationStartup;
	
	@Autowired
	private ReservationController reservationController;
	
	@Before
	public void setup() {
		this.mvc = standaloneSetup(reservationController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().is3xxRedirection());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", applicationStartup.reservationOne)).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addOccupant/1").flashAttr("guest", applicationStartup.guestOne)).andExpect(status().isForbidden());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/deleteContact/" + applicationStartup.contactOne.getId() + "/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().isForbidden());	

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(applicationStartup.reservationOne);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", applicationStartup.reservationOne)).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());

		mvc.perform(post("/addOccupant/1").flashAttr("guest", applicationStartup.guestOne)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().is4xxClientError());
		
		//in error due to constraint violation
		mvc.perform(post("/deleteContact/" + applicationStartup.contactOne.getId() + "/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().is4xxClientError());	
		
		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(applicationStartup.reservationOne);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1")).andExpect(status().is3xxRedirection());
		//TODO add addOccupant to these tests
		//		mvc.perform(post("/addOccupant/1")).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/dashboard")).andExpect(status().isOk());

		mvc.perform(post("/addOccupant/1").flashAttr("guest", applicationStartup.guestOne)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().is4xxClientError());

		//in error due to constraint violation
		mvc.perform(post("/deleteContact/" + applicationStartup.contactOne.getId() + "/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().is4xxClientError());	
		
		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(applicationStartup.reservationOne);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/{id}", Integer.valueOf(1))).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/deleteContact/" + applicationStartup.contactOne.getId() + "/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().isForbidden());	
		
		mvc.perform(post("/addOccupant/1").flashAttr("guest", applicationStartup.guestOne)).andExpect(status().isForbidden());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testFulfillReservation() throws Exception {
		bookingService.realiseReservation(applicationStartup.reservationOne);
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testSaveReservation() throws Exception {
		mvc.perform(post("/reservation/").flashAttr("reservation", applicationStartup.reservationOne)).andExpect(status().is3xxRedirection());
	}
}