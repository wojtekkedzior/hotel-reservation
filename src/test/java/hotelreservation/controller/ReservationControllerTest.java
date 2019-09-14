package hotelreservation.controller;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.ui.GuestDTO;
import hotelreservation.model.ui.ReservationCancellationDTO;
import hotelreservation.service.BookingService;
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

import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

	private GuestDTO guestDTO;
	private ReservationCancellationDTO cancellationDTO;

	@Before
	public void setup() {
		this.mvc = standaloneSetup(reservationController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context

		guestDTO = new GuestDTO("firstName", "lastName", "", applicationStartup.guestOne.getContact(), applicationStartup.guestOne.getIdentification());
		cancellationDTO = new ReservationCancellationDTO("reason");
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
		
		mvc.perform(post("/addOccupant/1").flashAttr("guestDTO", guestDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/deleteContact/" + applicationStartup.contactOne.getId() + "/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().isForbidden());	
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellationDTO", cancellationDTO)).andExpect(status().isForbidden());
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

		mvc.perform(post("/addOccupant/1").flashAttr("guestDTO", guestDTO)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().is4xxClientError());
		
		//in error due to constraint violation
		mvc.perform(post("/deleteContact/5/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().is4xxClientError());

		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellationDTO", cancellationDTO)).andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andDo(print()).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1")).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/dashboard")).andExpect(status().isOk());

		mvc.perform(post("/addOccupant/1").flashAttr("guestDTO", guestDTO)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().is4xxClientError());

		//in error due to constraint violation
		mvc.perform(post("/deleteContact/5/reservationId/" + applicationStartup.reservationOne.getId())).andExpect(status().is4xxClientError());
		
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellationDTO", cancellationDTO)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/{id}", 1)).andExpect(status().isForbidden());
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
		
		mvc.perform(post("/addOccupant/1").flashAttr("guestDTO", guestDTO)).andExpect(status().isForbidden());
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
		String collect = applicationStartup.reservationOne.getRoomRates().stream()
		        .map( n -> String.valueOf(n.getId()) )
		        .collect( Collectors.joining( "," ) );
		
		mvc.perform(post("/reservation/").flashAttr("reservation", applicationStartup.reservationOne).param("roomRateIds", collect))
				.andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testSaveReservationWithInvalidRates() throws Exception {
		mvc.perform(post("/reservation/").flashAttr("reservation", applicationStartup.reservationOne).param("roomRateIds", "123,123"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testRealiseReservation() throws Exception  {
		mvc.perform(post("/realiseReservation/" + applicationStartup.reservationOne.getId())).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testRealiseReservationWrongStatus() throws Exception  {
		mvc.perform(post("/realiseReservation/" + applicationStartup.reservationOne.getId())).andExpect(status().is3xxRedirection());
		mvc.perform(post("/realiseReservation/" + applicationStartup.reservationOne.getId())).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteReservation() throws Exception  {
		mvc.perform(delete("/reservationDelete/" + applicationStartup.reservationOne.getId())).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/reservationDelete/" + applicationStartup.reservationOne.getId())).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteNonExistentReservation() throws Exception  {
		mvc.perform(delete("/reservationDelete/" + 9999)).andDo(print()).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("manager")
	public void testGetReservationWithNoId() throws Exception {
		mvc.perform(get("/reservation/")).andExpect(status().isOk());
	}

}