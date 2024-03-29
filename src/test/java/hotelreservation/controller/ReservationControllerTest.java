package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.ui.GuestDTO;
import hotelreservation.model.ui.ReservationCancellationDTO;
import hotelreservation.model.ui.ReservationDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomRateService;

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
	private RoomRateService roomRateService;
	
	@Autowired
	private ApplicationStartup applicationStartup;
	
	@Autowired
	private ReservationController reservationController;

	private GuestDTO guestDTO;
	private ReservationCancellationDTO cancellationDTO;
	private ReservationDTO reservationDTO;

	@Before
	public void setup() {
		//resolves a circular dependancy when addReservations retruns the "reservation" view
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/view/");
 
		this.mvc = standaloneSetup(reservationController).setViewResolvers(viewResolver).setControllerAdvice(new RestExceptionHandler()).build();// Standalone context

		guestDTO = new GuestDTO("firstName", "lastName", "", applicationStartup.guestOne.getContact(), applicationStartup.guestOne.getIdentification());
		cancellationDTO = new ReservationCancellationDTO("reason");

		reservationDTO = new ReservationDTO(
			applicationStartup.reservationOne.getFirstName(),
			applicationStartup.reservationOne.getLastName(),
			applicationStartup.reservationOne.getOccupants(),
			applicationStartup.reservationOne.getStartDate(),
			applicationStartup.reservationOne.getEndDate(),
			applicationStartup.reservationOne.getReservationStatus());
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
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
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
	public void testFulfillNonExistantReservation() throws Exception {
		mvc.perform(post("/fulfillReservation/9999")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("manager")
	public void testSaveReservation() throws Exception {
		LocalDate startDate = LocalDate.of(2022, Month.MAY, 3);
		LocalDate endDate = LocalDate.of(2022, Month.MAY, 5);

		ReservationDTO reservationDTO = new ReservationDTO(
				applicationStartup.reservationOne.getFirstName(),
				applicationStartup.reservationOne.getLastName(),
				applicationStartup.reservationOne.getOccupants(),
				startDate,
				endDate,
				applicationStartup.reservationOne.getReservationStatus());

		String collect = roomRateService.getRoomRates(applicationStartup.standardRoomOne, startDate, endDate.minusDays(1)).stream()
		        .map( n -> String.valueOf(n.getId()) )
		        .collect(Collectors.joining( "," ));

		mvc.perform(post("/reservation/").flashAttr("reservationDTO", reservationDTO).param("roomRateIds", collect))
				.andExpect(status().is3xxRedirection());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testSaveReservationWithInvalidRates() throws Exception {
		mvc.perform(post("/reservation/").flashAttr("reservationDTO", reservationDTO).param("roomRateIds", "123,123"))
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
		mvc.perform(delete("/reservationDelete/9999")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("manager")
	public void testGetReservationWithNoId() throws Exception {
		mvc.perform(get("/reservation/ ")).andExpect(status().is4xxClientError());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testGetExistingReservationForEdit() throws Exception {
		mvc.perform(get("/editReservation/"+applicationStartup.reservationOne.getId())).andExpect(status().isOk()).andExpect(view().name("reservation"));
	}
	
	@Test
	@WithUserDetails("receptionist")
	public void testAddReservation() throws Exception {
		mvc.perform(get("/reservation")).andExpect(status().isOk())
		.andExpect(model().attributeExists("roomNumbers"))
		.andExpect(model().attributeExists("startDate"))
		.andExpect(model().attributeExists("endDate"))
		.andExpect(view().name("reservation"));
	}
	
	@Test
	@WithUserDetails("receptionist")
	public void testAddReservationWithDates() throws Exception {
		mvc.perform(get("/reservation/start/" + LocalDate.of(2022, Month.MAY, 1) + "/end/" + LocalDate.of(2022, Month.MAY, 2)))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("roomNumbers"))
		.andExpect(model().attributeExists("startDate"))
		.andExpect(model().attributeExists("endDate"))
		.andExpect(view().name("reservation"));
	}
}