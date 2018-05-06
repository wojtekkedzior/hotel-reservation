package hotelservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.Application;
import hotelreservation.DateConvertor;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Privilege;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.Role;
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
@SpringBootTest(classes = Application.class)
@DataJpaTest
@AutoConfigureMockMvc
// @WebMvcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD, hierarchyMode = DirtiesContext.HierarchyMode.EXHAUSTIVE)
public class ReservationControllerTest {

	private Role superAdminRole;
	private Role adminRole;

	private Role managerRole;
	private Role receptionistRole;

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

	// @Autowired
	// private DataSetup dataSetup;

	User admin;
	User manager;
	User receptionist;

	@Before
	public void setup() {
		addPrivileges();

		reservationOne = new Reservation();

		Contact contactTwo = new Contact();

		bookingService.createContact(contactTwo);

		Identification idTwo = new Identification(IdType.DriversLicense, "twoIdNumber");
		bookingService.createIdentification(idTwo);

		Guest mainGuest = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);
		bookingService.createGuest(mainGuest);

		User user = new User();
		// user.setUserType(managerUserType);
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

		List<RoomRate> roomRates = new ArrayList<>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		roomRates.add(roomRateFour);
		reservationOne.setRoomRates(roomRates);

		try {
			bookingService.createReservation(reservationOne);
		} catch (Exception e) {
			fail();
		}
	}

	private void addPrivileges() {
		// TODO Auto-generated method stub
		// Privilege realiseReservationPrivilege = createPrivilegeIfNotFound("REALISE_RESERVATION");
		// Privilege cancelReservationPrivilege = createPrivilegeIfNotFound("CANCEL_RESERVATION");

		adminRole = new Role("ADMIN", "admin desc", true);
		managerRole = new Role("MANAGER", "manager desc", true);
		receptionistRole = new Role("RECEPTIONIST", "receptionist", true);

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
		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		userService.createUser(admin);

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
		receptionist.setPassword("password");
		receptionist.setRoles(Arrays.asList(receptionistRole));
		receptionist.setEnabled(true);
		userService.createUser(receptionist);
	}

	private Privilege createPrivilegeIfNotFound(String name) {

		Privilege privilege = userService.getPrivilegeByName(name);
		if (privilege == null) {
			privilege = new Privilege(name);
			userService.createPrivilege(privilege);
		}
		return privilege;
	}

	// @Transactional
	// private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
	//
	// Role role = roleRepo.findByName(name);
	// if (role == null) {
	// role = new Role(name, "ada", true);
	// role.setPrivileges(privileges);
	// roleRepo.save(role);
	// }
	// return role;
	// }

	@Test
	public void testGetReservationPermissions() throws Exception {
		this.mockMvc.perform(get("/reservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/reservation/{id}", new Integer(1)).with(user("manager").roles("MISSING_ROLE"))).andExpect(status().isForbidden());

		this.mockMvc.perform(get("/reservation/{id}", new Integer(1)).with(user("manager").roles("MANAGER"))).andExpect(status().isOk());
		this.mockMvc.perform(get("/reservation/{id}", new Integer(1)).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().isOk());
	}

	@Test
	public void testRealiseReservationPermissions() throws Exception {
		this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("manager").roles("MISSING_ROLE"))).andExpect(status().isForbidden());

		this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("manager").roles("MANAGER"))).andExpect(status().isOk());
		this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().isOk());
	}
	

	@Test
	public void testPostRealiseReservationPermissions() throws Exception {
		mockMvc.perform(post("/realiseReservation").flashAttr("reservation", reservationOne).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		
		mockMvc.perform(post("/realiseReservation").flashAttr("reservation", reservationOne).with(user("manager").roles("MANAGER"))).andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/realiseReservation").flashAttr("reservation", reservationOne).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().is3xxRedirection());
	}

	@Test
	public void testCancelReservationPermissions() throws Exception {
		this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("manager").roles("MISSING_ROLE"))).andExpect(status().isForbidden());

		this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("manager").roles("MANAGER"))).andExpect(status().isOk());
		this.mockMvc.perform(get("/cancelReservation/{id}", new Integer(1)).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().isOk());
	}
	
	@Test
	public void testPostCancelReservationPermissions() throws Exception {
		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReservation(reservationOne);
		mockMvc.perform(post("/cancelReservation/{id}", new Integer(1)).flashAttr("reservation", cancellation).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		
		mockMvc.perform(post("/cancelReservation/{id}", new Integer(1)).flashAttr("reservation", cancellation).with(user("manager").roles("MANAGER"))).andExpect(status().is3xxRedirection());
		mockMvc.perform(post("/cancelReservation/{id}", new Integer(1)).flashAttr("reservation", cancellation).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().is3xxRedirection());
	}
	
	@Test
	public void testCheckoutReservationPermissions() throws Exception {
		this.mockMvc.perform(get("/checkoutReservation/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
		this.mockMvc.perform(get("/checkoutReservation/{id}", new Integer(1)).with(user("manager").roles("MISSING_ROLE"))).andExpect(status().isForbidden());

		this.mockMvc.perform(get("/checkoutReservation/{id}", new Integer(1)).with(user("manager").roles("MANAGER"))).andExpect(status().isOk());
		this.mockMvc.perform(get("/checkoutReservation/{id}", new Integer(1)).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().isOk());
		
		
		//TODO add post
	}

	@Test
	public void testDeleteReservationPermissions() throws Exception {
		this.mockMvc.perform(delete("/reservationDelete/{id}", new Integer(1)).with(user("manager").roles("MISSING_ROLE"))).andExpect(status().isForbidden());
		this.mockMvc.perform(delete("/reservationDelete/{id}", new Integer(1)).with(user("manager").roles("MANAGER"))).andExpect(status().isForbidden());
		this.mockMvc.perform(delete("/reservationDelete/{id}", new Integer(1)).with(user("receptionist").roles("RECEPTIONIST"))).andExpect(status().isForbidden());

		this.mockMvc.perform(delete("/reservationDelete/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().is3xxRedirection());
	}

	
	@Test
	public void testAddOccupantPermissions() throws Exception {

		this.mockMvc.perform(delete("/reservationDelete/{id}", new Integer(1)).with(user("admin").roles("ADMIN"))).andExpect(status().is3xxRedirection());
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// @WithMockUser(roles={"ADMIN"})
		// userService.methodTwo("This is Admin");

		// this.mockMvc.perform(get("/"))
		// .andDo(print())
		//// .andExpect(status().isOk())
		// .andExpect(view().name("redirect:/reservationDashBoard"));

		// mockMvc.perform(get("/reservationDashBoard"))
		// .andExpect(status().isForbidden()); // Expect that VIEWER users are forbidden from accessing this page
		//

		// mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/realiseReservation/{id}", Optional.of(1))
		// // ADD this line
		//// .with(user("admin").roles("USER","ADMIN"))
		//
		//
		//// .content(new ObjectMapper().writeValueAsString(passwordChange))
		// .contentType(MediaType.ALL)
		// .accept(MediaType.ALL))
		// .andExpect(status().isOk());

		// Forbidden
		// this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("test").roles("MISSING_ROLE")))
		// .andDo(print())
		// .andExpect(status().isForbidden());
		//
		// this.mockMvc.perform(get("/realiseReservation/{id}", new Integer(1)).with(user("test").roles("ADMIN")))
		// .andDo(print())
		// .andExpect(status().isOk());
		//

		// Send password change request
		// PasswordChangeRepresentation passwordChange = new PasswordChangeRepresentation(DefaultUsers.Admin.getPassword(), "12345678");
		// mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.POST, "/hotelreservation/realiseReservation")
		// // ADD this line
		// .with(user("admin").roles("USER","ADMIN"))
		//
		//// .content(new ObjectMapper().writeValueAsString(passwordChange))
		// .contentType(MediaType.ALL)
		// .accept(MediaType.ALL))
		// .andExpect(status().isOk());

		// Check that the password has been changed
		// User user = this.userRepository.findByUsername(DefaultUsers.Admin.getEmail());
		// assertEquals(user.getPassword(), "12345678");

}
