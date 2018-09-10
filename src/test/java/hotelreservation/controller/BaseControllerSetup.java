package hotelreservation.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import hotelreservation.Utils;
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
import hotelreservation.repository.UserRepo;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

public abstract class BaseControllerSetup {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PlatformTransactionManager txManager;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private Utils dateConvertor;
	
	private Role adminRole;
	private Role managerRole;
	private Role receptionistRole;

	private User admin;
	private User manager;
	private User receptionist;

	private Identification identification;
	
	protected User superAdmin;
	protected Reservation reservation;
	protected Guest guest;
	protected Contact contact;
	
	// Nasty hack - using @WithUserDetails causes the UserDetails service to be called as part of the security chain, which happens before the @Before.
	// Hopefully this will be fixed in some never version
	@BeforeTransaction
	public void setupExtra() {
		new TransactionTemplate(txManager).execute(status -> {
			createAdminUser();
			getPrivilegesForReceptionist();
			getPrivilegesForManager();
			getPrivilegesForAdmin();
			
			addPrivileges();
			addReservation();
			return null;
		});
	}
	
	abstract Collection<Privilege> getPrivilegesForReceptionist();
	abstract Collection<Privilege> getPrivilegesForManager();
	abstract Collection<Privilege> getPrivilegesForAdmin();

	private void addReservation() {
		contact = new Contact("some address", "cz");
		bookingService.createContact(contact);

		identification = new Identification(IdType.DriversLicense, "twoIdNumber");
		bookingService.createIdentification(identification);

		guest = new Guest("GuestTWo First Name", "GuestTwo Last Name", contact, identification);
		bookingService.createGuest(guest);

		reservation = new Reservation();
		reservation.setFirstName("firstName");
		reservation.setLastName("lastName");
		reservation.setCreatedBy(receptionist);
		reservation.setReservationStatus(ReservationStatus.UpComing);

		RoomType roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		Status operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);

		Room standardRoomOne = new Room(1, operational, roomTypeStandard, manager);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		roomService.saveRoom(standardRoomOne);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);
		roomService.saveRoomRate(roomRateFour);
		
		reservation.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservation.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		List<RoomRate> roomRates = new ArrayList<>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		roomRates.add(roomRateFour);
		reservation.setRoomRates(roomRates);
	}

	private void createAdminUser() {
		superAdmin = new User();
		superAdmin.setUserName("superAdmin");
		superAdmin.setFirstName("adminFirstName");
		superAdmin.setLastName("adminLastName");
		superAdmin.setCreatedOn(new Date());
		superAdmin.setPassword(passwordEncoder.encode("superAdminPassword"));
		userRepo.save(superAdmin);
	}

	private void addPrivileges() {
		Collection<Privilege> adminPrivileges = getPrivilegesForAdmin();
		Collection<Privilege> managerPrivileges = getPrivilegesForManager();
		Collection<Privilege> receptionistPrivileges = getPrivilegesForReceptionist();
		
		for (Privilege privilege : adminPrivileges) {
			userService.savePrivilege(privilege);
		}
		
		for (Privilege privilege : managerPrivileges) {
			userService.savePrivilege(privilege);
		}
		
		for (Privilege privilege : receptionistPrivileges) {
			userService.savePrivilege(privilege);
		}

		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		receptionistRole = new Role("receptionist", "receptionist", true);

		adminRole.setPrivileges(adminPrivileges);
		managerRole.setPrivileges(managerPrivileges);
		receptionistRole.setPrivileges(receptionistPrivileges);

		userService.saveRole(adminRole);
		userService.saveRole(managerRole);
		userService.saveRole(receptionistRole);

		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRoles(Arrays.asList(managerRole));
		userService.saveUser(manager, superAdmin.getUserName());

		admin = new User();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setUserName("admin");
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		userService.saveUser(admin, superAdmin.getUserName());

		receptionist = new User();
		receptionist.setFirstName("receptionist");
		receptionist.setLastName("receptionist");
		receptionist.setUserName("receptionist");
		receptionist.setPassword("password");
		receptionist.setRoles(Arrays.asList(receptionistRole));
		receptionist.setEnabled(true);
		userService.saveUser(receptionist, superAdmin.getUserName());
	}
}