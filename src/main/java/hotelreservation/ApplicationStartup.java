package hotelreservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hotelreservation.model.AmmenityType;
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
	
	private AmmenityType roomAmmenity;

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
		addRoomAmmenities();

		addRooms();
	}

	private void addRoomAmmenities() {
		// TODO Auto-generated method stub
		
	}

	private void addAmmenityTypes() {
		// TODO Auto-generated method stub
		
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
	
	private void addAmmenities() {
		
		
		
	}
	
	private void addRooms() {

		
	}

}