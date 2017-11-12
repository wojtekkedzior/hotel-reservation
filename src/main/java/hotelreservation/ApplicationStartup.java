package hotelreservation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hotelreservation.model.Ammenity;
import hotelreservation.model.AmmenityType;
import hotelreservation.model.Room;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
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

	private AmmenityType ammenityTypeRoomBasic;
	private AmmenityType ammenityTypeRoomLuxury;
	private AmmenityType ammenityTypeHotel;

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		addUserTypes();
		addUsers();

		addStatuses();

		addAmmenityTypes();
		addAmmenities();

		addRooms();
	}

	private void addAmmenities() {
		addHotelAmmenitites();
		addRoomAmmenitites();
	}

	private void addRoomAmmenitites() {
		Ammenity pillow = new Ammenity("pillow", "pillow", ammenityTypeRoomBasic);
		Ammenity phone = new Ammenity("phone", "phone", ammenityTypeRoomBasic);
		Ammenity blanket = new Ammenity("blanket", "blanket", ammenityTypeRoomBasic);
		Ammenity safebox = new Ammenity("safebox", "safebox", ammenityTypeRoomBasic);
		Ammenity tv = new Ammenity("tv", "tv", ammenityTypeRoomBasic);

		Ammenity hairDryer = new Ammenity("hairDryer", "hairDryer", ammenityTypeRoomLuxury);
		Ammenity miniBar = new Ammenity("miniBar", "miniBar", ammenityTypeRoomLuxury);
		Ammenity internet = new Ammenity("internet", "internet", ammenityTypeRoomLuxury);
		Ammenity rainShower = new Ammenity("rainShower", "rainShower", ammenityTypeRoomLuxury);
		Ammenity bathtub = new Ammenity("bathtub", "bathtub", ammenityTypeRoomLuxury);

		roomService.createAmmenity(pillow);
		roomService.createAmmenity(phone);
		roomService.createAmmenity(blanket);
		roomService.createAmmenity(safebox);
		roomService.createAmmenity(tv);

		roomService.createAmmenity(hairDryer);
		roomService.createAmmenity(miniBar);
		roomService.createAmmenity(internet);
		roomService.createAmmenity(rainShower);
		roomService.createAmmenity(bathtub);
	}

	private void addHotelAmmenitites() {
		Ammenity wifi = new Ammenity("wifi", "wifi", ammenityTypeHotel);
		Ammenity spaPool = new Ammenity("spaPool", "spaPool", ammenityTypeHotel);
		Ammenity pool = new Ammenity("pool", "pool", ammenityTypeHotel);
		Ammenity sauna = new Ammenity("sauna", "sauna", ammenityTypeHotel);
		Ammenity conferenceRoom = new Ammenity("conferenceRoom", "conferenceRoom", ammenityTypeHotel);

		roomService.createAmmenity(wifi);
		roomService.createAmmenity(spaPool);
		roomService.createAmmenity(pool);
		roomService.createAmmenity(sauna);
		roomService.createAmmenity(conferenceRoom);
	}

	private void addAmmenityTypes() {
		ammenityTypeRoomBasic = new AmmenityType("Basic", "Basic Room Ammenity Type");
		ammenityTypeRoomLuxury = new AmmenityType("Luxury", "Luxury Room Ammenity Type");
		ammenityTypeHotel = new AmmenityType("Hotel", "Hotel Ammenity Type");

		roomService.createAmmenityType(ammenityTypeRoomBasic);
		roomService.createAmmenityType(ammenityTypeRoomLuxury);
		roomService.createAmmenityType(ammenityTypeHotel);
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
		superAdmin = new User("superadmin", superAdminUserType, "Mr Super Admin");

		admin = new User("admin", adminUserType, "Mr Admin");
		admin.setCreatedBy(superAdmin);

		manager = new User("manager", managerUserType, "Mr Manager");
		manager.setCreatedBy(admin);

		receptionistOne = new User("receptionistOne", receptionUserType, "Mr Receptionist One");
		receptionistOne.setCreatedBy(admin);
		receptionistTwo = new User("receptionistTwo", receptionUserType, "Mr Receptionist Two");
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

	private void addRooms() {
		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		UserType managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		Room room = new Room();
		room.setRoomNumber(1);
		room.setName("Room 1");
		room.setDescription("The Best Room Description");
		room.setStatus(operational);
		room.setRoomType(roomType);
		room.setCreatedBy(admin);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);
	}

}