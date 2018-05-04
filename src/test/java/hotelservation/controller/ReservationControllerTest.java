package hotelservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hotelreservation.Application;
import hotelreservation.ApplicationStartup;
import hotelreservation.DateConvertor;
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
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest (classes = Application.class)
@DataJpaTest
@AutoConfigureMockMvc
public class ReservationControllerTest {
	
	  @Autowired
	    private MockMvc mockMvc;
	  
//	  @Autowired
//	  private ApplicationStartup startupData;
	  
	  @Before
	  public void setup() {
//		  StartUpData startupData = new StartUpData();
//		  startupData.run2();
		  
		  reservationOne = new Reservation();
		  
			Guest mainGuest = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);
			bookingService.createGuest(mainGuest);
			
			User user = new User();
//			user.setUserType(managerUserType);
			userService.createUser(user);

			reservationOne.setMainGuest(mainGuest);
			reservationOne.setCreatedBy(user);
			reservationOne.setReservationStatus(ReservationStatus.UpComing);
			
			standardRoomOne = new Room(1, operational, roomTypeStandard, user);
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
	    
	    
	    
	    
	    private Role superAdminRole;
		private Role adminUserRole;

		private Role managerUserRole;
		private Role receptionUserRole;

		private User superAdmin;
		private User admin;
		private User manager;
		private User receptionistOne;
		private User receptionistTwo;

		private Status operational;
		private Status underMaintenance;
		private Status underConstruction;
		private Status notOperational;

		private AmenityType amenityTypeRoomBasic;
		private AmenityType amenityTypeRoomLuxury;
		private AmenityType amenityTypeHotel;

		// Standard room amenities
		private Amenity pillow;
		private Amenity phone;
		private Amenity blanket;
		private Amenity safebox;
		private Amenity tv;

		// Standard room amenities
		private Amenity hairDryer;
		private Amenity miniBar;
		private Amenity internet;
		private Amenity rainShower;
		private Amenity bathtub;

		// Hotel amenities
		private Amenity wifi;
		private Amenity spaPool;
		private Amenity pool;
		private Amenity sauna;
		private Amenity conferenceRoom;

		private RoomType roomTypeStandard;
		private RoomType roomTypeLuxury;

		// Rooms
		private Room standardRoomOne;
		private Room standardRoomTwo;
		private Room standardRoomThree;
		private Room luxuryRoomOne;
		private Room luxuryRoomTwo;
		private Room luxuryRoomThree;

		// Contacts
		private Contact contactOne;
		private Contact contactTwo;
		private Contact contactThree;
		private Contact contactFour;

		// Identifications
		private Identification idOne;
		private Identification idTwo;
		private Identification idThree;
		private Identification idFour;

		// Guests
		private Guest guestOne;
		private Guest guestTwo;
		private Guest guestThree;
		private Guest guestFour;

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

		public void run() {
			addPrivileges();
//			addRoles();
//			addUsers();

			addStatuses();

			addAmenityTypes();
			addamenities();

			addRoomTypes();
			addRooms();

			addRoomRates();
			addContacts();
			addIdentifications();
			addGuests();

			addReservation();
			addReservations(6);
			createMultiRoomReservation();
		}

		private void addamenities() {
			addHotelamenitites();
			addRoomamenitites();
		}

		private void addRoomamenitites() {
			pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
			phone = new Amenity("phone", "phone", amenityTypeRoomBasic);
			blanket = new Amenity("blanket", "blanket", amenityTypeRoomBasic);
			safebox = new Amenity("safebox", "safebox", amenityTypeRoomBasic);
			tv = new Amenity("tv", "tv", amenityTypeRoomBasic);

			hairDryer = new Amenity("hairDryer", "hairDryer", amenityTypeRoomLuxury);
			miniBar = new Amenity("miniBar", "miniBar", amenityTypeRoomLuxury);
			internet = new Amenity("internet", "internet", amenityTypeRoomLuxury);
			rainShower = new Amenity("rainShower", "rainShower", amenityTypeRoomLuxury);
			bathtub = new Amenity("bathtub", "bathtub", amenityTypeRoomLuxury);

			roomService.createAmenity(pillow);
			roomService.createAmenity(phone);
			roomService.createAmenity(blanket);
			roomService.createAmenity(safebox);
			roomService.createAmenity(tv);

			roomService.createAmenity(hairDryer);
			roomService.createAmenity(miniBar);
			roomService.createAmenity(internet);
			roomService.createAmenity(rainShower);
			roomService.createAmenity(bathtub);
		}

		private void addHotelamenitites() {
			wifi = new Amenity("wifi", "wifi", amenityTypeHotel);
			spaPool = new Amenity("spaPool", "spaPool", amenityTypeHotel);
			pool = new Amenity("pool", "pool", amenityTypeHotel);
			sauna = new Amenity("sauna", "sauna", amenityTypeHotel);
			conferenceRoom = new Amenity("conferenceRoom", "conferenceRoom", amenityTypeHotel);

			roomService.createAmenity(wifi);
			roomService.createAmenity(spaPool);
			roomService.createAmenity(pool);
			roomService.createAmenity(sauna);
			roomService.createAmenity(conferenceRoom);
		}

		private void addAmenityTypes() {
			amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
			amenityTypeRoomLuxury = new AmenityType("Luxury", "Luxury Room amenity Type");
			amenityTypeHotel = new AmenityType("Hotel", "Hotel amenity Type");

			roomService.createAmenityType(amenityTypeRoomBasic);
			roomService.createAmenityType(amenityTypeRoomLuxury);
			roomService.createAmenityType(amenityTypeHotel);
		}
		
		private void addPrivileges() {
			// TODO Auto-generated method stub
			Privilege realiseReservationPrivilege = createPrivilegeIfNotFound("REALISE_RESERVATION");
			Privilege cancelReservationPrivilege = createPrivilegeIfNotFound("CANCEL_RESERVATION");
			
			
			superAdminRole = new Role("superAdmin", "superAdmin desc", true);

			adminUserRole = new Role("admin", "admin desc", true); 
			adminUserRole.setPrivileges(Arrays.asList(realiseReservationPrivilege,  cancelReservationPrivilege));

			managerUserRole = new Role("manager", "manager desc", true);
			managerUserRole.setPrivileges(Arrays.asList(realiseReservationPrivilege,  cancelReservationPrivilege));
			
			receptionUserRole = new Role("reception", "reception desc", true);
			receptionUserRole.setPrivileges(Arrays.asList(realiseReservationPrivilege,  cancelReservationPrivilege));

			
			userService.createRole(superAdminRole);
			userService.createRole(adminUserRole);

			userService.createRole(managerUserRole);
			userService.createRole(receptionUserRole);
			
			
			
			// SuperAdmin will be added by a script in the future
			superAdmin = new User("superadmin", "Mr Super Admin");
			superAdmin.setRoles(Arrays.asList(superAdminRole));

			admin = new User("admin", "Mr Admin");
			admin.setRoles(Arrays.asList(adminUserRole));
			
			manager = new User("manager", "Mr Manager");
			manager.setRoles(Arrays.asList(managerUserRole));
			
			receptionistOne = new User("receptionistOne", "Mr Receptionist One");
			receptionistOne.setRoles(Arrays.asList(receptionUserRole));
			
			receptionistTwo = new User("receptionistTwo", "Mr Receptionist Two");
			receptionistTwo.setRoles(Arrays.asList(receptionUserRole));

			
			admin.setCreatedBy(superAdmin);
			manager.setCreatedBy(admin);
			receptionistOne.setCreatedBy(admin);
			receptionistTwo.setCreatedBy(admin);

			userService.createUser(superAdmin);
			userService.createUser(admin, superAdminRole.getId());
			userService.createUser(manager, adminUserRole.getId());

			userService.createUser(receptionistOne, managerUserRole.getId());
			userService.createUser(receptionistTwo, managerUserRole.getId());
			
			
			
			
			
			
//			List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
//			createRoleIfNotFound("ROLE_SUPERADMIN", adminPrivileges);
//			createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
//			
//			createRoleIfNotFound("ROLE_MANAGER", adminPrivileges);
//			createRoleIfNotFound("ROLE_RECEPTIONIST", Arrays.asList(readPrivilege));
	//
////			Role adminRole = roleRepo.findByName("ROLE_ADMIN");
//			User user = new User();
//			user.setFirstName("Test");
//			user.setLastName("Test");
//			user.setUserName("test");
//			// user.setPassword(passwordEncoder.encode("test")); //TODO use encoder
//			user.setPassword("test");
//			user.setRoles(Arrays.asList(adminRole));
//			user.setEnabled(true);
////			userRepository.save(user);
//			
			
			
		}
		
		private Privilege createPrivilegeIfNotFound(String name) {

			Privilege privilege = userService.getPrivilegeByName(name);
			if (privilege == null) {
				privilege = new Privilege(name);
				userService.createPrivilege(privilege);
			}
			return privilege;
		}

//		private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
	//
//			Role role = userService.getRoleByName(name);
//			if (role == null) {
//				role = new Role(name, "ada", true);
//				role.setPrivileges(privileges);
//				userService.createRole(role);
//			}
//			return role;
//		}

		private void addRoles() {
			superAdminRole = new Role("superAdmin", "superAdmin desc", true);
			adminUserRole = new Role("admin2", "admin desc", true); //TODO needs better naming

			managerUserRole = new Role("manager", "manager desc", true);
			receptionUserRole = new Role("reception", "reception desc", true);

			userService.createRole(superAdminRole);
			userService.createRole(adminUserRole);

			userService.createRole(managerUserRole);
			userService.createRole(receptionUserRole);
		}

		private void addUsers() {
			// SuperAdmin will be added by a script in the future
			superAdmin = new User("superadmin", "Mr Super Admin");

			admin = new User("admin", "Mr Admin");
			manager = new User("manager", "Mr Manager");
			receptionistOne = new User("receptionistOne", "Mr Receptionist One");
			receptionistTwo = new User("receptionistTwo", "Mr Receptionist Two");

			admin.setCreatedBy(superAdmin);
			manager.setCreatedBy(admin);
			receptionistOne.setCreatedBy(admin);
			receptionistTwo.setCreatedBy(admin);

			userService.createUser(superAdmin);
			userService.createUser(admin, superAdminRole.getId());
			userService.createUser(manager, adminUserRole.getId());

			userService.createUser(receptionistOne, managerUserRole.getId());
			userService.createUser(receptionistTwo, managerUserRole.getId());
		}

		private void addStatuses() {
			operational = new Status("Operational", "Room is in operation");
			underMaintenance = new Status("Under Maintenance", "Room is under maintenance");
			underConstruction = new Status("Under Conctruction", "Room is under cunstruction");
			notOperational = new Status("Not Operational", "Room is not operational");

			roomService.createStatus(operational);
			roomService.createStatus(underMaintenance);
			roomService.createStatus(underConstruction);
			roomService.createStatus(notOperational);
		}

		private void addRoomTypes() {
			roomTypeStandard = new RoomType("Standard", "Standard room");
			roomTypeLuxury = new RoomType("Luxury", "Luxury room");

			roomService.createRoomType(roomTypeStandard);
			roomService.createRoomType(roomTypeLuxury);
		}

		private void addRooms() {
			standardRoomOne = new Room(1, operational, roomTypeStandard, admin);
			standardRoomOne.setName("Room 1");
			standardRoomOne.setDescription("The Best Room Description");
			List<Amenity> standardRoomOneAmenitites = new ArrayList<>();
			standardRoomOneAmenitites.add(pillow);
			standardRoomOneAmenitites.add(phone);
			standardRoomOneAmenitites.add(blanket);
			standardRoomOne.setRoomAmenities(standardRoomOneAmenitites);
			roomService.createRoom(standardRoomOne);

			standardRoomTwo = new Room(2, operational, roomTypeStandard, admin);
			standardRoomTwo.setName("Room 2");
			standardRoomTwo.setDescription("The Second Best Room Description");
			List<Amenity> standardRoomTwoAmenitites = new ArrayList<>();
			standardRoomTwoAmenitites.add(pillow);
			standardRoomTwoAmenitites.add(safebox);
			standardRoomTwoAmenitites.add(blanket);
			standardRoomTwo.setRoomAmenities(standardRoomTwoAmenitites);
			roomService.createRoom(standardRoomTwo);
			
			standardRoomThree = new Room(3, operational, roomTypeStandard, admin);
			standardRoomThree.setName("Room 3");
			standardRoomThree.setDescription("The Third Best Room Description");
			standardRoomThree.setRoomAmenities(standardRoomTwoAmenitites);
			roomService.createRoom(standardRoomThree);

			// Luxury Rooms
			luxuryRoomOne = new Room(11, operational, roomTypeLuxury, admin);
			luxuryRoomOne.setName("Room 11");
			luxuryRoomOne.setDescription("The Best Luxury Room Description");
			List<Amenity> luxuryRoomOneAmenitites = new ArrayList<>();
			luxuryRoomOneAmenitites.add(pillow);
			luxuryRoomOneAmenitites.add(phone);
			luxuryRoomOneAmenitites.add(blanket);
			luxuryRoomOneAmenitites.add(internet);
			luxuryRoomOneAmenitites.add(rainShower);
			luxuryRoomOne.setRoomAmenities(luxuryRoomOneAmenitites);
			roomService.createRoom(luxuryRoomOne);

			luxuryRoomTwo = new Room(22, operational, roomTypeLuxury, admin);
			luxuryRoomTwo.setName("Room 22");
			luxuryRoomTwo.setDescription("The Second Best Luxury Room Description");
			List<Amenity> luxuryRoomTwoAmenitites = new ArrayList<>();
			luxuryRoomTwoAmenitites.add(pillow);
			luxuryRoomTwoAmenitites.add(safebox);
			luxuryRoomTwoAmenitites.add(blanket);
			luxuryRoomTwoAmenitites.add(bathtub);
			luxuryRoomTwoAmenitites.add(miniBar);
			luxuryRoomTwo.setRoomAmenities(luxuryRoomTwoAmenitites);
			roomService.createRoom(luxuryRoomTwo);
			
			luxuryRoomThree = new Room(33, operational, roomTypeLuxury, admin);
			luxuryRoomThree.setName("Room 33");
			luxuryRoomThree.setDescription("The Thid Best Luxury Room Description");
			luxuryRoomThree.setRoomAmenities(luxuryRoomTwoAmenitites);
			roomService.createRoom(luxuryRoomThree);
		}

		private void addRoomRates() {
			Calendar cal = new GregorianCalendar();
			cal.setTime(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));

			for (int days = 1; days <= 365; days++) {
				cal.roll(Calendar.DAY_OF_YEAR, true);

				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				int value = 1000;

				if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
					value = 1999;
				} else if (dayOfWeek == Calendar.SUNDAY) {
					value = 1500;
				}

				roomService.createRoomRate(new RoomRate(standardRoomOne, Currency.CZK, value, cal.getTime()));
				roomService.createRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, value, cal.getTime()));
				roomService.createRoomRate(new RoomRate(standardRoomThree, Currency.CZK, value, cal.getTime()));

				roomService.createRoomRate(new RoomRate(luxuryRoomOne, Currency.CZK, value * 2, cal.getTime()));
				roomService.createRoomRate(new RoomRate(luxuryRoomTwo, Currency.CZK, value * 2, cal.getTime()));
				roomService.createRoomRate(new RoomRate(luxuryRoomThree, Currency.CZK, value * 2, cal.getTime()));
			}
		}

		private void addContacts() {
			contactOne = new Contact("address1", "Country1");
			contactTwo = new Contact("address2", "Country2");
			contactThree = new Contact("address3", "Country3");
			contactFour = new Contact("address4", "Country4");

			bookingService.createContact(contactOne);
			bookingService.createContact(contactTwo);
			bookingService.createContact(contactThree);
			bookingService.createContact(contactFour);
		}

		private void addIdentifications() {
			idOne = new Identification(IdType.IDCard, "oneIdNumber");
			idTwo = new Identification(IdType.DriversLicense, "twoIdNumber");
			idThree = new Identification(IdType.Passport, "threeIdNumber");
			idFour = new Identification(IdType.IDCard, "fourIdNumber");

			bookingService.createIdentification(idOne);
			bookingService.createIdentification(idTwo);
			bookingService.createIdentification(idThree);
			bookingService.createIdentification(idFour);
		}

		private void addGuests() {
			guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", contactOne, idOne);
			guestTwo = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);
			guestThree = new Guest("GuestThree First Name", "GuestThree Last Name", contactThree, idThree);
			guestFour = new Guest("GuestFour First Name", "GuestFour Last Name", contactFour, idFour);

			bookingService.createGuest(guestOne);
			bookingService.createGuest(guestTwo);
			bookingService.createGuest(guestThree);
			bookingService.createGuest(guestFour);
		}

		private void addReservation() {
			LocalDate startDate = LocalDate.of(2018, Month.MARCH, 3);
			LocalDate endDate = LocalDate.of(2018, Month.MARCH, 20);

			reservationOne = new Reservation();
			reservationOne.setStartDate(dateConvertor.asDate(startDate));
			reservationOne.setEndDate(dateConvertor.asDate(endDate));
			reservationOne.setMainGuest(guestOne);
			reservationOne.setOccupants(Arrays.asList(guestTwo, guestThree));
			reservationOne.setRoomRates(new ArrayList<RoomRate>());

			List<RoomRate> roomRatesForAllRooms = roomService.getRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.MARCH, 1)),
					dateConvertor.asDate(LocalDate.of(2018, Month.MARCH, 31)));

			for (RoomRate roomRate : roomRatesForAllRooms) {
				if (roomRate.getRoom().getId() == 1 
						&& roomRate.getDay().after(dateConvertor.asDate(startDate.minusDays(1)))
						&& roomRate.getDay().before(dateConvertor.asDate(endDate.plusDays(1)))) {
					reservationOne.getRoomRates().add(roomRate);
				}
			}

			bookingService.createReservation(reservationOne);
		}
		
		private void addReservations(int reservations) {
			
			for (int i = 2; i <= reservations; i++) {
				LocalDate startDate = LocalDate.of(2018, Month.MARCH, 3);
				LocalDate endDate = LocalDate.of(2018, Month.MARCH, 20);

				Reservation reservation = new Reservation();
				reservation.setStartDate(dateConvertor.asDate(startDate));
				reservation.setEndDate(dateConvertor.asDate(endDate));
				
				reservation.setMainGuest(guestOne);
				reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
				reservation.setRoomRates(new ArrayList<RoomRate>());

				List<RoomRate> roomRatesForAllRooms = roomService.getAvailableRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.MARCH, 1)),
						dateConvertor.asDate(LocalDate.of(2018, Month.MARCH, 31)));

				for (RoomRate roomRate : roomRatesForAllRooms) {
					if (roomRate.getRoom().getId() == i // this is the room ID 
							&& roomRate.getDay().after(dateConvertor.asDate(startDate.minusDays(1)))
							&& roomRate.getDay().before(dateConvertor.asDate(endDate.plusDays(1)))) {
						reservation.getRoomRates().add(roomRate);
					}
				}
				
				bookingService.createReservation(reservation);	
			}
			
			//Make one reservation InProgress
			Reservation reservation = bookingService.getReservation(3);
			reservation.setReservationStatus(ReservationStatus.InProgress);
			bookingService.saveReservation(reservation);
			
		}
		
		private void createMultiRoomReservation () {
			LocalDate startDate = LocalDate.of(2018, Month.APRIL, 1);
			LocalDate endDate = LocalDate.of(2018, Month.APRIL, 5);
			
			Reservation reservation = new Reservation();
			reservation.setStartDate(dateConvertor.asDate(startDate));
			reservation.setEndDate(dateConvertor.asDate(endDate));
			reservation.setMainGuest(guestOne);
			reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
			reservation.setRoomRates(new ArrayList<RoomRate>());

			List<RoomRate> roomRatesForAllRooms = roomService.getAvailableRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 1)),
					dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 3)));

			for (RoomRate roomRate : roomRatesForAllRooms) {
				if (roomRate.getRoom().getId() == 1 
						&& roomRate.getDay().after(dateConvertor.asDate(startDate.minusDays(1)))
						&& roomRate.getDay().before(dateConvertor.asDate(endDate.plusDays(1)))) {
					reservation.getRoomRates().add(roomRate);
				}
			}
			
			roomRatesForAllRooms = roomService.getAvailableRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 4)),
					dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 5)));

			for (RoomRate roomRate : roomRatesForAllRooms) {
				if (roomRate.getRoom().getId() == 2 
						&& roomRate.getDay().after(dateConvertor.asDate(startDate.minusDays(1)))
						&& roomRate.getDay().before(dateConvertor.asDate(endDate.plusDays(1)))) {
					reservation.getRoomRates().add(roomRate);
				}
			}
			
			bookingService.createReservation(reservation);	
		}


}
