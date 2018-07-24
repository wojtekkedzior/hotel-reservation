package hotelservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hotelreservation.Application;
import hotelreservation.Utils;
import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Privilege;
import hotelreservation.model.Reservation;
import hotelreservation.model.Role;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.finance.Payment;
import hotelreservation.service.BookingService;
import hotelreservation.service.MyUserDetailsService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceControllerTest {

	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User admin;
	private User manager;
	private User receptionist;

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

	@Autowired
	private PlatformTransactionManager txManager;

	// Nasty hack - using @WithUserDetails causes the UserDetails service to be called as part of the securtiy chain, which happens before the @Before.
	// Hopefully this will be fixed in some never version
	@BeforeTransaction
	public void setup1() {
		new TransactionTemplate(txManager).execute(status -> {
			addPrivileges();
			return null;
		});
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
		// user.setUserType(managerUserType);
		userService.createUser(user);

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomType roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomTypeStandard);

		Status operational = new Status("Operational", "Room is in operation");
		roomService.createStatus(operational);

		AmenityType amenityType = new AmenityType("room amenity", "comfort");
		roomService.createAmenityType(amenityType);

		Room standardRoomOne = new Room(1, operational, roomTypeStandard, user);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		// List<Amenity> roomAmenities = new ArrayList<Amenity>();
		// Amenity pillow = new Amenity("pillow", "pillow", amenityType);
		standardRoomOne.setRoomAmenities(Arrays.asList(new Amenity("pillow", "pillow", amenityType)));
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
		
		bookingService.saveReservation(reservationOne);

		try {
			// bookingService.createReservation(reservationOne);
		} catch (Exception e) {
			fail();
		}
	}

	private void addPrivileges() {
		Privilege createPayment = new Privilege("createPayment");

		userService.createPrivilege(createPayment);

		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();

		managerPrivileges.add(createPayment);
		receptionistPrivileges.add(createPayment);

		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		receptionistRole = new Role("receptionist", "receptionist", true);

		adminRole.setPrivileges(adminPrivileges);
		managerRole.setPrivileges(managerPrivileges);
		receptionistRole.setPrivileges(receptionistPrivileges);

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

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
//		mvc.perform(get("/amenity/1")).andExpect(status().isOk());
//		mvc.perform(get("/amenityType/1")).andExpect(status().isOk());
//		
//		mvc.perform(get("/room/1")).andExpect(status().isOk());
//		mvc.perform(get("/roomType/1")).andExpect(status().isOk());
		
		mvc.perform(post("/createPayment/1").flashAttr("payment", new Payment())).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate())).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
//		mvc.perform(get("/roomRate/1")).andExpect(status().isOk());
//		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate(new Room(), Currency.CZK, 10, new Date()))).andExpect(status().is3xxRedirection());
		mvc.perform(post("/createPayment/1").flashAttr("payment", new Payment())).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
//		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
//		
//		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
//		
//		mvc.perform(post("/addAmenityType").flashAttr("amenityType", new AmenityType())).andExpect(status().isForbidden());
//		mvc.perform(post("/addAmenity").flashAttr("amenity", new Amenity())).andExpect(status().isForbidden());
//
//		mvc.perform(post("/addRoomType").flashAttr("roomType", new RoomType())).andExpect(status().isForbidden());
//		mvc.perform(post("/addRoom").flashAttr("room", new Room())).andExpect(status().isForbidden());
//		
//		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
//		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
//		
//		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
//		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
//		
//		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		mvc.perform(post("/createPayment/1").flashAttr("payment", new Payment())).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", new AmenityType())).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenity", new Amenity())).andExpect(status().isForbidden());

		mvc.perform(post("/addRoomType").flashAttr("roomType", new RoomType())).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("room", new Room())).andExpect(status().isForbidden());
		
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate(new Room(), Currency.CZK, 10, new Date()))).andExpect(status().isForbidden());
		
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
//		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/createPayment/1").flashAttr("payment", new Payment())).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testCantCreatePaymentWithNoCharges() throws Exception {
		mvc.perform(post("/createPayment/1").flashAttr("payment", new Payment())).andExpect(status().isPreconditionRequired());
	}
}