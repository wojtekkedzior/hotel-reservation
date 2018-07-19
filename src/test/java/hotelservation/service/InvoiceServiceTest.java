package hotelservation.service;

import static org.junit.Assert.assertEquals;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
import hotelreservation.DateConvertor;
import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Charge;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Privilege;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
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
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class InvoiceServiceTest {

	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PrivilegeRepo privilegeRepo;
	
	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;
	
	private Role role;
	private User user;
	private Privilege priv1;
	private Privilege priv2;
	
	private Guest mainGuest;
	private Guest guestOne;
	
	private Reservation reservationOne;
	
	
	private Room standardRoomOne;
	private Room standardRoomTwo;
	private Room standardRoomThree;
	private RoomType roomTypeStandard;
	
	private Date startDate;
	private Date endDate;
	
	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private Role managerUserType;
	
	private Identification idOne;
	private Identification idTwo;

	private Contact contactOne;
	private Contact contactTwo;
	
	@Autowired
	private DateConvertor dateConvertor;
	
	private List<Privilege> privileges;

	@Before
	public void setup() {
		role = new Role("receptionistRole", "desc", true);
		roleRepo.save(role);
		
		user = new User("receptionist", "bobalina");
		Collection<Role> roles = new ArrayList<Role>();
		roles.add(role);
		user.setRoles(roles);
		user.setPassword("password");
		
		userRepo.save(user);
		
		priv1 = new Privilege("priv1");
		priv2 = new Privilege("priv2");
		
		privilegeRepo.save(priv1);
		privilegeRepo.save(priv2);
		
		privileges = new ArrayList<Privilege>();
		privileges.add(priv1);
		privileges.add(priv2);
		
		
		managerUserType = new Role("manager", "manager desc", true);
		userService.createRole(managerUserType);

		user = new User();
//		user.setUserType(managerUserType);
		userService.createUser(user);

		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		roomService.createAmenityType(amenityTypeRoomBasic);

		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
		roomService.createAmenity(pillow);

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomTypeStandard);

		operational = new Status("Operational", "Room is in operation");
		roomService.createStatus(operational);

		standardRoomOne = new Room(1, operational, roomTypeStandard, user);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Arrays.asList(pillow));
		roomService.createRoom(standardRoomOne);
		
		standardRoomTwo = new Room(2, operational, roomTypeStandard, user);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Arrays.asList(pillow));
		roomService.createRoom(standardRoomTwo);
		
		standardRoomThree = new Room(3, operational, roomTypeStandard, user);
		standardRoomThree.setName("Room 3");
		standardRoomThree.setDescription("The Best Room Description");
		standardRoomThree.setRoomAmenities(Arrays.asList(pillow));
		roomService.createRoom(standardRoomThree);

		idOne = new Identification(IdType.IDCard, "oneIdNumber");
		idTwo = new Identification(IdType.DriversLicense, "twoIdNumber");

		bookingService.createIdentification(idOne);
		bookingService.createIdentification(idTwo);

		contactOne = new Contact();
		contactTwo = new Contact();

		bookingService.createContact(contactOne);
		bookingService.createContact(contactTwo);

		guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", contactOne, idOne);
		mainGuest = new Guest("GuestTWo First Name", "GuestTwo Last Name", contactTwo, idTwo);

		bookingService.createGuest(guestOne);
		bookingService.createGuest(mainGuest);
		
		startDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1));
		endDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 31));
	}
	
	@Test
	public void testGetOutstandingCharges() {
		reservationOne = new Reservation();
		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		bookingService.createReservation(reservationOne);
		
		Charge chargeOne = new Charge(Currency.CZK, 100);
		Charge chargeTwo = new Charge(Currency.CZK, 200);
		Charge chargeThree = new Charge(Currency.CZK, 300);
		
		invoiceService.createCharge(chargeOne);
		invoiceService.createCharge(chargeTwo);
		invoiceService.createCharge(chargeThree);
		
		ReservationCharge reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		ReservationCharge reservationChargeTwo = new ReservationCharge();
		reservationChargeTwo.setCharge(chargeTwo);
		reservationChargeTwo.setQuantity(1);
		reservationChargeTwo.setReservation(reservationOne);
		
		invoiceService.saveChargeToReservation(reservationChargeOne);
		invoiceService.saveChargeToReservation(reservationChargeTwo);
		
		//Two unpaid charges for the given reservation.
		assertEquals(2, invoiceService.getOutstandingCharges(reservationOne).size());
		
		Payment payment = new Payment();
		payment.setReservation(reservationOne);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne));
		invoiceService.savePayment(payment);
		
		//1 unpaid charge and 1 paid charge for the given reservation.
		assertEquals(1, invoiceService.getOutstandingCharges(reservationOne).size());
	}
	
	@Test
	public void testGetAllReservationChargesForReservation() {
		reservationOne = new Reservation();
		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		bookingService.createReservation(reservationOne);
		
		Charge chargeOne = new Charge(Currency.CZK, 100);
		Charge chargeTwo = new Charge(Currency.CZK, 200);
		Charge chargeThree = new Charge(Currency.CZK, 300);
		
		invoiceService.createCharge(chargeOne);
		invoiceService.createCharge(chargeTwo);
		invoiceService.createCharge(chargeThree);
		
		ReservationCharge reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		ReservationCharge reservationChargeTwo = new ReservationCharge();
		reservationChargeTwo.setCharge(chargeTwo);
		reservationChargeTwo.setQuantity(1);
		reservationChargeTwo.setReservation(reservationOne);
		
		invoiceService.saveChargeToReservation(reservationChargeOne);
		invoiceService.saveChargeToReservation(reservationChargeTwo);
		
		assertEquals(2, invoiceService.getAllReservationCharges(reservationOne).size());
	}
}
