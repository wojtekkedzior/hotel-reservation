package hotelservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.Application;
import hotelreservation.DateConvertor;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest (classes = Application.class)
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationControllerTest {
	
	  @Autowired
	    private MockMvc mockMvc;
	  
		// Reservations
		private Reservation reservationOne;

		@Autowired
		private UserService userService;

		@Autowired
		private RoomService roomService;

		@Autowired
		private BookingService bookingService;

		@Autowired
		private DateConvertor dateConvertor;
		
//		@Autowired
//		private DataSetup dataSetup;
		
	  @Before
	  public void setup() {
		  reservationOne = new Reservation();
		  
		  Contact contactTwo = new Contact();

			bookingService.createContact(contactTwo);
			
			Identification idTwo = new Identification(IdType.DriversLicense, "twoIdNumber");
			bookingService.createIdentification(idTwo);
			
			Guest mainGuest = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);
			bookingService.createGuest(mainGuest);
			
			User user = new User();
//			user.setUserType(managerUserType);
			userService.createUser(user);

			reservationOne.setMainGuest(mainGuest);
			reservationOne.setCreatedBy(user);
			reservationOne.setReservationStatus(ReservationStatus.UpComing);
			
			RoomType roomTypeStandard = new RoomType("Standard", "Standard room");
			roomService.createRoomType(roomTypeStandard);

			Status operational = new Status("Operational", "Room is in operation");
			roomService.createStatus(operational);
			
			Room standardRoomOne = new Room(1, operational, roomTypeStandard, user);
			standardRoomOne.setName("Room 1");
			standardRoomOne.setDescription("The Best Room Description");
			roomService.createRoom(standardRoomOne);

			RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
			RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
			RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

			roomService.createRoomRate(roomRateTwo);
			roomService.createRoomRate(roomRateThree);
			roomService.createRoomRate(roomRateFour);

			reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
			reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

			reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree, roomRateFour));

			try {
				bookingService.createReservation(reservationOne);
			} catch (Exception e) {
				fail();
			}
	  }
	  
	   @Test
	    public void testCancelReservationPermissions() throws Exception {
	        this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("admin").roles("MANAGER")))
	        .andDo(print())
	        .andExpect(status().isForbidden()); 
	        
//	        this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("admin").roles("MANAGER")))
//	        .andDo(print())
//	        .andExpect(status().isForbidden()); 
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
	        
	        System.err.println("");
	        
	        
	        this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN")))
	        .andDo(print())
	        .andExpect(status().isOk()); 
	        
	        
	    	
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
