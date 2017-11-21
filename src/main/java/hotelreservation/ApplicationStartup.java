package hotelreservation;

import java.util.ArrayList;
import java.util.Date;
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
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
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
	
	//Standard room amenities
	private Amenity pillow;
	private Amenity phone;
	private Amenity blanket;
	private Amenity safebox;
	private Amenity tv;
	
	//Standard room amenities
	private Amenity hairDryer;
	private Amenity miniBar;
	private Amenity internet;
	private Amenity rainShower;
	private Amenity bathtub;
	
	//Hotel amenities
	private Amenity wifi;
	private Amenity spaPool;
	private Amenity pool;
	private Amenity sauna;
	private Amenity conferenceRoom;
	
	
	private RoomType roomTypeStandard;
	private RoomType roomTypeLuxury;
	
	//Rooms
	private Room standardRoomOne;
	private Room standardRoomTwo;
	private Room luxuryRoomOne;
	private Room luxuryRoomTwo;
	
	//Contacts
	private Contact contactOne;
	private Contact contactTwo;
	private Contact contactThree;
	private Contact contactFour;
	
	//Identifications
	private Identification idOne;
	private Identification idTwo;
	private Identification idThree;
	private Identification idFour;
	
	//Guests
	private Guest guestOne;
	private Guest guestTwo;
	private Guest guestThree;
	private Guest guestFour;
	
	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;
	
	@Autowired
	private BookingService bookingService;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		addUserTypes();
		addUsers();

		addStatuses();

		addAmenityTypes();
		addamenities();

		addRoomTypes();
		addRooms();
		
		addRoomRate();
		addContacts();
		addIdentifications();
		addGuests();
	
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
		//SuperAdmin will be added by a script in the future
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
		userService.createUser(admin, superAdmin.getId());
		userService.createUser(manager, admin.getId());

		userService.createUser(receptionistOne, manager.getId());
		userService.createUser(receptionistTwo, manager.getId());
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
		 
		
		//Luxury Rooms
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
	}
	
	private void addRoomRate() {
		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, new Date(), new Date());
		roomService.createRoomRate(roomRateOne);
	}
	
	private void addContacts() {
		contactOne = new Contact();
		contactTwo = new Contact();
		contactThree = new Contact();
		contactFour = new Contact();
		
		bookingService.createContact(contactOne);
		bookingService.createContact(contactTwo);
		bookingService.createContact(contactThree);
		bookingService.createContact(contactFour);
	}
	
	private void addIdentifications() {
		idOne = new Identification("IdOne Name", "IdOne Description", IdType.IDCard);
		idTwo = new Identification("IdTwo Name", "IdTwo Description", IdType.DriversLicense);
		idThree = new Identification("IdThree Name", "IdThree Description", IdType.Passport);
		idFour = new Identification("IdFour Name", "IdFour Description", IdType.IDCard);
		
		bookingService.createIdentification(idOne);
		bookingService.createIdentification(idTwo);
		bookingService.createIdentification(idThree);
		bookingService.createIdentification(idFour);
	}
	
	private void addGuests() {
		guestOne = new Guest("GuestOne Name", "GuestOne Description", contactOne, idOne);
		guestTwo = new Guest("GuestTWo Name", "GuestTwo Description", contactTwo, idTwo);
		guestThree = new Guest("GuestThree Name", "GuestThree Description", contactThree, idThree);
		guestFour = new Guest("GuestFour Name", "GuestFour Description", contactFour, idFour);
		
		bookingService.createGuest(guestOne);
		bookingService.createGuest(guestTwo);
		bookingService.createGuest(guestThree);
		bookingService.createGuest(guestFour);
	}
	

}