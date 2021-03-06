package hotelreservation;

import hotelreservation.model.*;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
	private static final int YEAR = 2019;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User superAdmin;
	public User admin;
	private User manager;
	private User receptionist;

	public Status operational;
	private Status underMaintenance;
	private Status underConstruction;
	private Status notOperational;

	public AmenityType amenityTypeRoomBasic;
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

	public RoomType roomTypeStandard;
	private RoomType roomTypeLuxury;

	// Rooms
	public Room standardRoomOne;
	private Room standardRoomTwo;
	private Room standardRoomThree;
	private Room luxuryRoomOne;
	private Room luxuryRoomTwo;
	private Room luxuryRoomThree;

	// Contacts
	public Contact contactOne;
	private Contact contactTwo;
	private Contact contactThree;
	private Contact contactFour;

	// Identifications
	private Identification idOne;
	private Identification idTwo;
	private Identification idThree;
	private Identification idFour;

	// Guests
	public Guest guestOne;
	private Guest guestTwo;
	private Guest guestThree;
	private Guest guestFour;
	
	//Charges
	public Charge coke;
	public Charge roomServiceDelivery;
	private Charge brokenTable;

	// Reservations
	public Reservation reservationOne;

	private final UserService userService;
	private final RoomService roomService;
	private final RoomRateService roomRateService;
	private final BookingService bookingService;
	private final InvoiceService invoiceService;
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.debug("loading test data - start");

		addPrivileges();
		addStatuses();
		addCharges();

		addAmenityTypes();
		addAmenities();

		addRoomTypes();
		addRooms();

		addRoomRates();
		addContacts();
		addIdentifications();
		addGuests();

		addReservation();
		addReservations();
		createMultiRoomReservation();

		log.debug("loading test data - end");
	}
	
	private void addCharges() {
		coke = new Charge(Currency.CZK, 50, "coke", "coke desc");
		roomServiceDelivery = new Charge(Currency.CZK, 1000, "room service delivery", "room service delivery desc");
		brokenTable = new Charge(Currency.CZK, 5000, "broken table", "a very broke table");
		
		invoiceService.saveCharge(coke);
		invoiceService.saveCharge(roomServiceDelivery);
		invoiceService.saveCharge(brokenTable);
	}

	private void addAmenities() {
		addHotelamenitites();
		addRoomamenitites();
	}
	
	private void addPrivileges() {
		Privilege createAmenity = new Privilege("createAmenity");
		Privilege createAmenityType = new Privilege("createAmenityType");
		Privilege createRoom = new Privilege("createRoom");
		Privilege createRoomType = new Privilege("createRoomType");
		Privilege createRoomRate = new Privilege("createRoomRate");
		Privilege deleteAmenity = new Privilege("deleteAmenity");
		Privilege deleteAmenityType = new Privilege("deleteAmenityType");
		Privilege deleteRoom = new Privilege("deleteRoom");
		Privilege deleteRoomType = new Privilege("deleteRoomType");
		Privilege deleteRoomRate = new Privilege("deleteRoomRate");
		Privilege getReservation = new Privilege("getReservation");
		Privilege createReservation = new Privilege("createReservation");
		Privilege cancelReservation = new Privilege("cancelReservation");
		Privilege realiseReservation = new Privilege("realiseReservation");
		Privilege checkoutReservation = new Privilege("checkoutReservation");
		Privilege fulfillReservation = new Privilege("fulfillReservation");
		Privilege deleteReservation = new Privilege("deleteReservation");
		Privilege createUser = new Privilege("createUser");
		Privilege deleteUser = new Privilege("deleteUser");
		Privilege deleteUserRole = new Privilege("deleteUserRole");
		Privilege viewAdmin = new Privilege("viewAdmin");
		Privilege viewReservationDashBoard = new Privilege("viewReservationDashBoard");
		Privilege createPayment = new Privilege("createPayment");
		
		userService.savePrivilege(createAmenity);
		userService.savePrivilege(createAmenityType);
		userService.savePrivilege(createRoom);
		userService.savePrivilege(createRoomType);
		userService.savePrivilege(createRoomRate);
		userService.savePrivilege(deleteAmenity);
		userService.savePrivilege(deleteAmenityType);
		userService.savePrivilege(deleteRoom);
		userService.savePrivilege(deleteRoomType);
		userService.savePrivilege(deleteRoomRate);
		userService.savePrivilege(getReservation);
		userService.savePrivilege(createReservation);
		userService.savePrivilege(cancelReservation);
		userService.savePrivilege(realiseReservation);
		userService.savePrivilege(checkoutReservation);
		userService.savePrivilege(fulfillReservation);
		userService.savePrivilege(deleteReservation);
		userService.savePrivilege(viewAdmin);
		userService.savePrivilege(createUser);
		userService.savePrivilege(deleteUser);
		userService.savePrivilege(deleteUserRole);
		userService.savePrivilege(viewReservationDashBoard);
		userService.savePrivilege(createPayment);

		Collection<Privilege> adminPrivileges = new ArrayList<>();
		Collection<Privilege> managerPrivileges = new ArrayList<>();
		Collection<Privilege> receptionistPrivileges = new ArrayList<>();

		adminPrivileges.add(deleteRoom);
		adminPrivileges.add(deleteRoomType);
		adminPrivileges.add(deleteAmenity);
		adminPrivileges.add(deleteRoomRate);
		adminPrivileges.add(deleteAmenityType);
		adminPrivileges.add(createAmenity);
		adminPrivileges.add(createAmenityType);
		adminPrivileges.add(createRoom);
		adminPrivileges.add(createRoomType);
		adminPrivileges.add(deleteReservation);
		adminPrivileges.add(viewAdmin);
		adminPrivileges.add(createUser);
		adminPrivileges.add(deleteUser);
		adminPrivileges.add(deleteUserRole);
		adminPrivileges.add(viewReservationDashBoard);
		
		managerPrivileges.add(getReservation);
		managerPrivileges.add(createReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(realiseReservation);
		managerPrivileges.add(cancelReservation);
		managerPrivileges.add(checkoutReservation);
		managerPrivileges.add(fulfillReservation);
		managerPrivileges.add(viewAdmin);
		managerPrivileges.add(createRoomRate);
		managerPrivileges.add(createUser);
		managerPrivileges.add(viewReservationDashBoard);
		managerPrivileges.add(createPayment);
		
		receptionistPrivileges.add(getReservation);
		receptionistPrivileges.add(createReservation);
		receptionistPrivileges.add(cancelReservation);
		receptionistPrivileges.add(realiseReservation);
		receptionistPrivileges.add(cancelReservation);
		receptionistPrivileges.add(checkoutReservation);
		receptionistPrivileges.add(fulfillReservation);
		receptionistPrivileges.add(viewReservationDashBoard);
		receptionistPrivileges.add(createPayment);
		
		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		receptionistRole = new Role("receptionist", "receptionist", true);

		adminRole.setPrivileges(adminPrivileges);
		managerRole.setPrivileges(managerPrivileges);
		receptionistRole.setPrivileges(receptionistPrivileges);

		userService.saveRole(adminRole);
		userService.saveRole(managerRole);
		userService.saveRole(receptionistRole);

		superAdmin = new User();
		superAdmin.setUserName("superAdmin");
		superAdmin.setFirstName("firstName");
		superAdmin.setLastName("lastName");
		superAdmin.setRole(adminRole);
		superAdmin.setCreatedOn(LocalDateTime.now());
		superAdmin.setPassword(passwordEncoder.encode("password"));
		userRepo.save(superAdmin);

		admin = new User();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setUserName("admin");
		admin.setPassword("password");
		admin.setRole(adminRole);
		admin.setEnabled(true);
		admin.setCreatedBy(superAdmin);
		userService.saveUser(admin, superAdmin.getUserName());
		
		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRole(managerRole);
		manager.setCreatedBy(admin);
		userService.saveUser(manager, superAdmin.getUserName());

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		receptionist.setPassword("password");
		receptionist.setRole(receptionistRole);
		receptionist.setEnabled(true);
		receptionist.setCreatedBy(manager);
		userService.saveUser(receptionist, manager.getUserName());
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

		roomService.saveAmenity(pillow);
		roomService.saveAmenity(phone);
		roomService.saveAmenity(blanket);
		roomService.saveAmenity(safebox);
		roomService.saveAmenity(tv);

		roomService.saveAmenity(hairDryer);
		roomService.saveAmenity(miniBar);
		roomService.saveAmenity(internet);
		roomService.saveAmenity(rainShower);
		roomService.saveAmenity(bathtub);
	}

	private void addHotelamenitites() {
		wifi = new Amenity("wifi", "wifi", amenityTypeHotel);
		spaPool = new Amenity("spaPool", "spaPool", amenityTypeHotel);
		pool = new Amenity("pool", "pool", amenityTypeHotel);
		sauna = new Amenity("sauna", "sauna", amenityTypeHotel);
		conferenceRoom = new Amenity("conferenceRoom", "conferenceRoom", amenityTypeHotel);

		roomService.saveAmenity(wifi);
		roomService.saveAmenity(spaPool);
		roomService.saveAmenity(pool);
		roomService.saveAmenity(sauna);
		roomService.saveAmenity(conferenceRoom);
	}

	private void addAmenityTypes() {
		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		amenityTypeRoomLuxury = new AmenityType("Luxury", "Luxury Room amenity Type");
		amenityTypeHotel = new AmenityType("Hotel", "Hotel amenity Type");

		roomService.saveAmenityType(amenityTypeRoomBasic);
		roomService.saveAmenityType(amenityTypeRoomLuxury);
		roomService.saveAmenityType(amenityTypeHotel);
	}

	private void addStatuses() {
		operational = new Status("Operational", "Room is in operation");
		underMaintenance = new Status("Under Maintenance", "Room is under maintenance");
		underConstruction = new Status("Under Conctruction", "Room is under cunstruction");
		notOperational = new Status("Not Operational", "Room is not operational");

		roomService.saveStatus(operational);
		roomService.saveStatus(underMaintenance);
		roomService.saveStatus(underConstruction);
		roomService.saveStatus(notOperational);
	}

	private void addRoomTypes() {
		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomTypeLuxury = new RoomType("Luxury", "Luxury room");

		roomService.saveRoomType(roomTypeStandard);
		roomService.saveRoomType(roomTypeLuxury);
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
		roomService.saveRoom(standardRoomOne);

		standardRoomTwo = new Room(2, operational, roomTypeStandard, admin);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Second Best Room Description");
		List<Amenity> standardRoomTwoAmenitites = new ArrayList<>();
		standardRoomTwoAmenitites.add(pillow);
		standardRoomTwoAmenitites.add(safebox);
		standardRoomTwoAmenitites.add(blanket);
		standardRoomTwo.setRoomAmenities(standardRoomTwoAmenitites);
		roomService.saveRoom(standardRoomTwo);
		
		standardRoomThree = new Room(3, operational, roomTypeStandard, admin);
		standardRoomThree.setName("Room 3");
		standardRoomThree.setDescription("The Third Best Room Description");
		standardRoomThree.setRoomAmenities(standardRoomTwoAmenitites);
		roomService.saveRoom(standardRoomThree);

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
		roomService.saveRoom(luxuryRoomOne);

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
		roomService.saveRoom(luxuryRoomTwo);
		
		luxuryRoomThree = new Room(33, operational, roomTypeLuxury, admin);
		luxuryRoomThree.setName("Room 33");
		luxuryRoomThree.setDescription("The Thid Best Luxury Room Description");
		luxuryRoomThree.setRoomAmenities(luxuryRoomTwoAmenitites);
		roomService.saveRoom(luxuryRoomThree);
	}

	private void addRoomRates() {
		LocalDate date = LocalDate.of(2019, Month.JANUARY, 1);
		int value = 1000;

		for (int days = 1; days <= 730; days++) {
			if (date.getDayOfWeek().equals(DayOfWeek.FRIDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
				value = 1999;
			} else if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
				value = 1500;
			}

			roomRateService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, value, date));
			roomRateService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, value, date));
			roomRateService.saveRoomRate(new RoomRate(standardRoomThree, Currency.CZK, value, date));

			roomRateService.saveRoomRate(new RoomRate(luxuryRoomOne, Currency.CZK, value * 2, date));
			roomRateService.saveRoomRate(new RoomRate(luxuryRoomTwo, Currency.CZK, value * 2, date));
			roomRateService.saveRoomRate(new RoomRate(luxuryRoomThree, Currency.CZK, value * 2, date));
			
			date = date.plus(1,  ChronoUnit.DAYS);
			value = 1000;
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
		idOne = new Identification(IdType.ID_CARD, "oneIdNumber");
		idTwo = new Identification(IdType.DRIVERS_LICENSE, "twoIdNumber");
		idThree = new Identification(IdType.PASSPORT, "threeIdNumber");
		idFour = new Identification(IdType.ID_CARD, "fourIdNumber");

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
		LocalDate startDate = LocalDate.of(YEAR, Month.MARCH, 3);
		LocalDate endDate = LocalDate.of(YEAR, Month.MARCH, 20);

		reservationOne = new Reservation();
		reservationOne.setStartDate(startDate);
		reservationOne.setEndDate(endDate);
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setOccupants(Arrays.asList(guestTwo, guestThree));
		reservationOne.setRoomRates(new ArrayList<>());
		reservationOne.setCreatedBy(manager);

		List<RoomRate> roomRatesForAllRooms = roomRateService.getRoomRates(LocalDate.of(YEAR, Month.MARCH, 1), LocalDate.of(YEAR, Month.MARCH, 31));
		
		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 1 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate)) {
				
				reservationOne.getRoomRates().add(roomRate);
			}
		}
		bookingService.saveReservation(reservationOne);
	}
	
	private void addReservations() {
		for (int i = 2; i <= 6; i++) {
			LocalDate startDate = LocalDate.of(YEAR, Month.MARCH, 3);
			LocalDate endDate = LocalDate.of(YEAR, Month.MARCH, 20);

			Reservation reservation = new Reservation();
			reservation.setStartDate(startDate);
			reservation.setEndDate(endDate);
			reservation.setFirstName("firstName");
			reservation.setLastName("lastName"); 
			reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
			reservation.setRoomRates(new ArrayList<>());
			reservation.setCreatedBy(manager);

			List<RoomRate> roomRatesForAllRooms = roomRateService.getAvailableRoomRates(LocalDate.of(YEAR, Month.MARCH, 1), LocalDate.of(YEAR, Month.MARCH, 31));

			for (RoomRate roomRate : roomRatesForAllRooms) {
				if (roomRate.getRoom().getId() == i // this is the room ID 
						&& roomRate.getDay().isAfter(startDate.minusDays(1))
						&& roomRate.getDay().isBefore(endDate)) {
					reservation.getRoomRates().add(roomRate);
				}
			}
			
			bookingService.saveReservation(reservation);	
		}
		
		//Make one reservation InProgress
		Reservation reservation = bookingService.getReservation(3L);
		bookingService.realiseReservation(reservation);
	}
	
	private void createMultiRoomReservation () {
		LocalDate startDate = LocalDate.of(YEAR, Month.APRIL, 1);
		LocalDate endDate = LocalDate.of(YEAR, Month.APRIL, 5);
		
		Reservation reservation = new Reservation();
		reservation.setStartDate(startDate);
		reservation.setEndDate(endDate);
		reservation.setFirstName("firstName");
		reservation.setLastName("lastName");
		reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
		reservation.setRoomRates(new ArrayList<>());
		reservation.setCreatedBy(manager);

		List<RoomRate> roomRatesForAllRooms = roomRateService.getRoomRates(LocalDate.of(YEAR, Month.APRIL, 1), LocalDate.of(YEAR, Month.APRIL, 4));
		
		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 1 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate.plusDays(1))) {
				reservation.getRoomRates().add(roomRate);
			}
		}
		
		roomRatesForAllRooms = roomRateService.getAvailableRoomRates(LocalDate.of(YEAR, Month.APRIL, 4), LocalDate.of(YEAR, Month.APRIL, 5));

		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 2 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate.plusDays(1))) {
				reservation.getRoomRates().add(roomRate);
			}
		}
		
		bookingService.saveReservation(reservation);	
	}
}