package hotelreservation;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@Component
@Profile("dev")
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	private UserType superAdminUserType;
	private UserType adminUserType;

	private UserType managerUserType;
	private UserType receptionUserType;

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

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		addUserTypes();
		addUsers();

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

	private void addUserTypes() {
		superAdminUserType = new UserType("superAdmin", "superAdmin desc", true);
		adminUserType = new UserType("admin", "admin desc", true);

		managerUserType = new UserType("manager", "manager desc", true);
		receptionUserType = new UserType("reception", "reception desc", true);

		userService.createUserType(superAdminUserType);
		userService.createUserType(adminUserType);

		userService.createUserType(managerUserType);
		userService.createUserType(receptionUserType);
	}

	private void addUsers() {
		// SuperAdmin will be added by a script in the future
		superAdmin = new User("superadmin", superAdminUserType, "Mr Super Admin");

		admin = new User("admin", adminUserType, "Mr Admin");
		manager = new User("manager", managerUserType, "Mr Manager");
		receptionistOne = new User("receptionistOne", receptionUserType, "Mr Receptionist One");
		receptionistTwo = new User("receptionistTwo", receptionUserType, "Mr Receptionist Two");

		admin.setCreatedBy(superAdmin);
		manager.setCreatedBy(admin);
		receptionistOne.setCreatedBy(admin);
		receptionistTwo.setCreatedBy(admin);

		userService.createUser(superAdmin);
		userService.createUser(admin, superAdminUserType.getId());
		userService.createUser(manager, adminUserType.getId());

		userService.createUser(receptionistOne, managerUserType.getId());
		userService.createUser(receptionistTwo, managerUserType.getId());
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
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
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
			reservation.setReservationStatus(ReservationStatus.UpComing);
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
	}
	
	private void createMultiRoomReservation () {
		LocalDate startDate = LocalDate.of(2018, Month.APRIL, 1);
		LocalDate endDate = LocalDate.of(2018, Month.APRIL, 5);
		
		Reservation reservation = new Reservation();
		reservation.setStartDate(dateConvertor.asDate(startDate));
		reservation.setEndDate(dateConvertor.asDate(endDate));
		reservation.setReservationStatus(ReservationStatus.UpComing);
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