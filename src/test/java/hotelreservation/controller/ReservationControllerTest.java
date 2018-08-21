package hotelreservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.Application;
import hotelreservation.Utils;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Privilege;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.service.BookingService;
import hotelreservation.service.MyUserDetailsService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationControllerTest extends BaseControllerSetup {

	private Reservation reservationOne;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private Utils dateConvertor;
	
	@Override
	Collection<Privilege> getPrivilegesForReceptionist() {
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();
		receptionistPrivileges.add(new Privilege("getReservation"));
		receptionistPrivileges.add(new Privilege("createReservation"));
		receptionistPrivileges.add(new Privilege("cancelReservation"));
		receptionistPrivileges.add(new Privilege("realiseReservation"));
		receptionistPrivileges.add(new Privilege("checkoutReservation"));
		receptionistPrivileges.add(new Privilege("fulfillReservation"));
		receptionistPrivileges.add(new Privilege("viewReservationDashBoard"));
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
		managerPrivileges.add(new Privilege("fulfillReservation"));
		managerPrivileges.add(new Privilege("viewReservationDashBoard"));
		return managerPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForAdmin() {
		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		adminPrivileges.add(new Privilege("deleteReservation"));
		adminPrivileges.add(new Privilege("viewReservationDashBoard"));
		return adminPrivileges;
	}

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
		user.setPassword("password");
		user.setUserName("username");
		userService.saveUser(user, superAdmin.getUserName());

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomType roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		Status operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);

		Room standardRoomOne = new Room(1, operational, roomTypeStandard, user);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		roomService.saveRoom(standardRoomOne);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);
		roomService.saveRoomRate(roomRateFour);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		List<RoomRate> roomRates = new ArrayList<>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		roomRates.add(roomRateFour);
		reservationOne.setRoomRates(roomRates);

		try {
			bookingService.saveReservation(reservationOne);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().is3xxRedirection());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", reservationOne)).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		mvc.perform(post("/cancelReservation/1").flashAttr("reservation", new ReservationCancellation())).andExpect(status().isForbidden());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", reservationOne)).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReservation(reservationOne);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservation", cancellation)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().is4xxClientError());
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
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReservation(reservationOne);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservation", cancellation)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/{id}", new Integer(1))).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
	}
}