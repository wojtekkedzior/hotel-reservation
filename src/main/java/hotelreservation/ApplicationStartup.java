package hotelreservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Charge;
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
import hotelreservation.repository.AmenityRepo;
import hotelreservation.repository.AmenityTypeRepo;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.ReservationRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.RoomRateRepo;
import hotelreservation.repository.RoomRepo;
import hotelreservation.repository.StatusRepo;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.RoomRateService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@Profile("asdasdas")
@RequiredArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
	private static final int YEAR = 2022;

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
		log.info("loading test data - start");

		log.info("startPriv");
		addPrivileges();
		log.info("endPriv");
		addStatuses();
		log.info("endstatus");
		addCharges();
		log.info("endChargers");

		addAmenityTypes();
		log.info("end ammentityTypes");
		addAmenities();
		log.info("end ammentirtes");

		addRoomTypes();
		log.info("end roomtTypes");
		addRooms();
		log.info("end rooms");

		addRoomRates();
		log.info("end roomsrates");
		addContacts();
		addIdentifications();
		addGuests();
		
		addAllReservations();

		log.info("loading test data - end");
	}
	
	private void addAllReservations() {
		List<Reservation> reservations = new ArrayList<>();
		reservations.add(addReservation());
		reservations.addAll(addReservations());
		reservations.add(createMultiRoomReservation());
		reservationRepo.saveAll(reservations);
		
		//Make one reservation InProgress
		Reservation reservation1 = bookingService.getReservation(3L);
		bookingService.realiseReservation(reservation1);
	}
	
	private void addCharges() {
		coke = new Charge(Currency.CZK, 50, "coke", "coke desc");
		roomServiceDelivery = new Charge(Currency.CZK, 1000, "room service delivery", "room service delivery desc");
		brokenTable = new Charge(Currency.CZK, 5000, "broken table", "a very broke table");
		
		invoiceService.saveCharge(coke);
		invoiceService.saveCharge(roomServiceDelivery);
		invoiceService.saveCharge(brokenTable);
	}

	private final PrivilegeRepo privilegeRepo;
	private final AmenityRepo amenityRepo;
	private final AmenityTypeRepo amenityTypeRepo;
	private final StatusRepo statusRepo;
	private final RoomRepo roomRepo;
	private final RoomRateRepo roomRateRepo;
	private final RoleRepo roleRepo;
	private final ContactRepo contactRepo;
	private final IdentificationRepo identificationRepo;
	private final GuestRepo guestRepo;
	private final ReservationRepo reservationRepo;
	
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
		
		List<Privilege> privileges = Arrays.asList(createAmenity, createAmenityType, createRoom, createRoomType, createRoomRate, deleteAmenity, deleteAmenityType, deleteRoom,deleteRoomType,
				deleteRoomRate, getReservation, createReservation, cancelReservation, realiseReservation, checkoutReservation, fulfillReservation, deleteReservation, createUser, deleteUser,
				deleteUserRole, viewAdmin,  viewReservationDashBoard, createPayment);
		
		// This is slow than the JDBC template, but it's easier as the lists are reused later on.  Stuff written using the updateBatch function is not connected to the Hibernate session
		// So i'd have to go and fetch all the privileges. This whole functions costs about 5s in AWS
		privilegeRepo.saveAll(privileges);
		
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

		roleRepo.saveAll(Arrays.asList(adminRole, managerRole, receptionistRole));

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
	
	private void addAmenities() {
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
		
		// addHotelamenitites
		wifi = new Amenity("wifi", "wifi", amenityTypeHotel);
		spaPool = new Amenity("spaPool", "spaPool", amenityTypeHotel);
		pool = new Amenity("pool", "pool", amenityTypeHotel);
		sauna = new Amenity("sauna", "sauna", amenityTypeHotel);
		conferenceRoom = new Amenity("conferenceRoom", "conferenceRoom", amenityTypeHotel);

		List<Amenity> amenities = Arrays.asList(pillow, phone, blanket, safebox, tv, hairDryer, miniBar, internet, rainShower, bathtub, wifi, spaPool, pool, sauna, conferenceRoom);
		amenityRepo.saveAll(amenities);
	}

	private void addAmenityTypes() {
		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		amenityTypeRoomLuxury = new AmenityType("Luxury", "Luxury Room amenity Type");
		amenityTypeHotel = new AmenityType("Hotel", "Hotel amenity Type");

		List<AmenityType> amenityTypes = Arrays.asList(amenityTypeRoomBasic, amenityTypeRoomLuxury, amenityTypeHotel);
		amenityTypeRepo.saveAll(amenityTypes);
	}

	private void addStatuses() {
		operational = new Status("Operational", "Room is in operation");
		underMaintenance = new Status("Under Maintenance", "Room is under maintenance");
		underConstruction = new Status("Under Conctruction", "Room is under cunstruction");
		notOperational = new Status("Not Operational", "Room is not operational");
		
		List<Status> statuses = Arrays.asList(operational, underMaintenance, underConstruction, notOperational);
		statusRepo.saveAll(statuses);
	}

	private void addRoomTypes() {
		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomTypeLuxury = new RoomType("Luxury", "Luxury room");

		roomService.saveRoomType(roomTypeStandard);
		roomService.saveRoomType(roomTypeLuxury);
	}

	private void addRooms() {
		List<Amenity> standardRoomAmenitites = Arrays.asList(pillow, safebox, blanket);
		List<Amenity> luxuryRoomAmenitites = Arrays.asList(pillow, phone, safebox, blanket, internet, rainShower);
		
		standardRoomOne = new Room(1, operational, roomTypeStandard, admin);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(standardRoomAmenitites);

		standardRoomTwo = new Room(2, operational, roomTypeStandard, admin);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Second Best Room Description");
		standardRoomTwo.setRoomAmenities(standardRoomAmenitites);
		
		standardRoomThree = new Room(3, operational, roomTypeStandard, admin);
		standardRoomThree.setName("Room 3");
		standardRoomThree.setDescription("The Third Best Room Description");
		standardRoomThree.setRoomAmenities(standardRoomAmenitites);

		// Luxury Rooms
		luxuryRoomOne = new Room(11, operational, roomTypeLuxury, admin);
		luxuryRoomOne.setName("Room 11");
		luxuryRoomOne.setDescription("The Best Luxury Room Description");
		luxuryRoomOne.setRoomAmenities(luxuryRoomAmenitites);

		luxuryRoomTwo = new Room(22, operational, roomTypeLuxury, admin);
		luxuryRoomTwo.setName("Room 22");
		luxuryRoomTwo.setDescription("The Second Best Luxury Room Description");
		luxuryRoomTwo.setRoomAmenities(luxuryRoomAmenitites);
		
		luxuryRoomThree = new Room(33, operational, roomTypeLuxury, admin);
		luxuryRoomThree.setName("Room 33");
		luxuryRoomThree.setDescription("The Thid Best Luxury Room Description");
		luxuryRoomThree.setRoomAmenities(luxuryRoomAmenitites);
		
		roomRepo.saveAll(Arrays.asList(standardRoomOne, standardRoomTwo, standardRoomThree, luxuryRoomOne, luxuryRoomTwo, luxuryRoomThree));
	}
    
	private void addRoomRates() {
		LocalDate date = LocalDate.of(2022, Month.MAY, 1);
		int value = 1000;
		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		
		for (int days = 1; days <= 730; days++) {
			if (date.getDayOfWeek().equals(DayOfWeek.FRIDAY) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
				value = 1999;
			} else if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
				value = 1500;
			}
			
			roomRates.add(new RoomRate(standardRoomOne, Currency.CZK, value, date));
			roomRates.add(new RoomRate(standardRoomTwo, Currency.CZK, value, date));
			roomRates.add(new RoomRate(standardRoomThree, Currency.CZK, value, date));
			
			roomRates.add(new RoomRate(luxuryRoomOne, Currency.CZK, value * 2, date));
			roomRates.add(new RoomRate(luxuryRoomTwo, Currency.CZK, value * 2, date));
			roomRates.add(new RoomRate(luxuryRoomThree, Currency.CZK, value * 2, date));

			date = date.plus(1,  ChronoUnit.DAYS);
			value = 1000;
		}
		
		roomRateRepo.saveAll(roomRates);
//	    String sql = "INSERT INTO `room_rate` (ROOM_ID, CURRENCY, VALUE, DAY) VALUES(?,?,?,?)";
//	    StopWatch timer = new StopWatch();
//	    timer.start(); 
//	    log.info("batchInsert start");
//	    
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//          @Override
//          public void setValues(PreparedStatement ps, int i) throws SQLException {
//        	  
//        	RoomRate roomRate = roomRates.get(i);
//            ps.setLong(1, roomRate.getRoom().getId());
//            ps.setString(2, roomRate.getCurrency().toString());
//            ps.setLong(3, roomRate.getValue());
//            ps.setDate(4, java.sql.Date.valueOf(roomRate.getDay()));
//          }
//          @Override
//          public int getBatchSize() {
//            return roomRates.size();
//          }
//        });
//	    
//        timer.stop();
//        log.info("batchInsert -> Total time in seconds: " + timer.getTotalTimeSeconds());
//        
//        List<RoomRate> findAll = (List<RoomRate>) roomRateRepo.findAll();
//        log.error("found: " + findAll.size());
        
//        roomRateService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, date.plusWeeks(5)));
        
	}
	
	private void addContacts() {
		contactOne = new Contact("address1", "Country1");
		contactTwo = new Contact("address2", "Country2");
		contactThree = new Contact("address3", "Country3");
		contactFour = new Contact("address4", "Country4");

		contactRepo.saveAll(Arrays.asList(contactOne, contactTwo, contactThree, contactFour));
	}

	private void addIdentifications() {
		idOne = new Identification(IdType.ID_CARD, "oneIdNumber");
		idTwo = new Identification(IdType.DRIVERS_LICENSE, "twoIdNumber");
		idThree = new Identification(IdType.PASSPORT, "threeIdNumber");
		idFour = new Identification(IdType.ID_CARD, "fourIdNumber");
		
		identificationRepo.saveAll(Arrays.asList(idOne, idTwo, idThree, idFour));
	}

	private void addGuests() {
		guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", contactOne, idOne);
		guestTwo = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);
		guestThree = new Guest("GuestThree First Name", "GuestThree Last Name", contactThree, idThree);
		guestFour = new Guest("GuestFour First Name", "GuestFour Last Name", contactFour, idFour);

		guestRepo.saveAll(Arrays.asList(guestOne, guestTwo, guestThree, guestFour));
	}

	private Reservation addReservation() {
		LocalDate startDate = LocalDate.of(YEAR, Month.JUNE, 3);
		LocalDate endDate = LocalDate.of(YEAR, Month.JUNE, 20);

		reservationOne = new Reservation();
		reservationOne.setStartDate(startDate);
		reservationOne.setEndDate(endDate);
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setOccupants(Arrays.asList(guestTwo, guestThree));
		reservationOne.setRoomRates(new ArrayList<>());
		reservationOne.setCreatedBy(manager);
		reservationOne.setReservationStatus(ReservationStatus.UP_COMING);
		reservationOne.setCreatedOn(LocalDateTime.now());

		List<RoomRate> roomRatesForAllRooms = roomRateService.getRoomRates(LocalDate.of(YEAR, Month.JUNE, 1), LocalDate.of(YEAR, Month.JUNE, 30));
		
		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 1 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate)) {
				
				reservationOne.getRoomRates().add(roomRate);
			}
		}
		
		return reservationOne;
	}
	
	private List<Reservation> addReservations() {
		List<Reservation> reservations = new ArrayList<>();
		
		for (int i = 2; i <= 6; i++) {
			LocalDate startDate = LocalDate.of(YEAR, Month.JUNE, 3);
			LocalDate endDate = LocalDate.of(YEAR, Month.JUNE, 20);

			Reservation reservation = new Reservation();
			reservation.setStartDate(startDate);
			reservation.setEndDate(endDate);
			reservation.setFirstName("firstName");
			reservation.setLastName("lastName"); 
			reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
			reservation.setRoomRates(new ArrayList<>());
			reservation.setCreatedBy(manager);
			reservation.setReservationStatus(ReservationStatus.UP_COMING);
			reservation.setCreatedOn(LocalDateTime.now());

			List<RoomRate> roomRatesForAllRooms = roomRateService.getAvailableRoomRates(LocalDate.of(YEAR, Month.JUNE, 1), LocalDate.of(YEAR, Month.JUNE, 30));

			for (RoomRate roomRate : roomRatesForAllRooms) {
				if (roomRate.getRoom().getId() == i // this is the room ID 
						&& roomRate.getDay().isAfter(startDate.minusDays(1))
						&& roomRate.getDay().isBefore(endDate)) {
					reservation.getRoomRates().add(roomRate);
				}
			}

			reservations.add(reservation);
		}
		
		return reservations;
	}
	
	private Reservation createMultiRoomReservation () {
		LocalDate startDate = LocalDate.of(YEAR, Month.JULY, 1);
		LocalDate endDate = LocalDate.of(YEAR, Month.JULY, 5);
		
		Reservation reservation = new Reservation();
		reservation.setStartDate(startDate);
		reservation.setEndDate(endDate);
		reservation.setFirstName("firstName");
		reservation.setLastName("lastName");
		reservation.setOccupants(Arrays.asList(guestTwo, guestThree));
		reservation.setRoomRates(new ArrayList<>());
		reservation.setCreatedBy(manager);
		reservation.setReservationStatus(ReservationStatus.UP_COMING);
		reservation.setCreatedOn(LocalDateTime.now());

		List<RoomRate> roomRatesForAllRooms = roomRateService.getRoomRates(LocalDate.of(YEAR, Month.JULY, 1), LocalDate.of(YEAR, Month.JULY, 4));
		
		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 1 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate.plusDays(1))) {
				reservation.getRoomRates().add(roomRate);
			}
		}
		
		roomRatesForAllRooms = roomRateService.getAvailableRoomRates(LocalDate.of(YEAR, Month.JULY, 4), LocalDate.of(YEAR, Month.JULY, 5));

		for (RoomRate roomRate : roomRatesForAllRooms) {
			if (roomRate.getRoom().getId() == 2 
					&& roomRate.getDay().isAfter(startDate.minusDays(1))
					&& roomRate.getDay().isBefore(endDate.plusDays(1))) {
				reservation.getRoomRates().add(roomRate);
			}
		}
		
		return reservation;
	}
}