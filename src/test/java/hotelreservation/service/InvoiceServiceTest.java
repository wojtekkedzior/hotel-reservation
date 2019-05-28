package hotelreservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
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
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.finance.Payment;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InvoiceServiceTest extends BaseServiceTest {

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
	
//	private Guest mainGuest;
	private Guest guestOne;
	
	private Reservation reservationOne;
	
	private Room standardRoomOne;
	private Room standardRoomTwo;
	private Room standardRoomThree;
	private RoomType roomTypeStandard;
	
	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private Role managerUserType;
	
	private Identification idOne;
	private Identification idTwo;

	private Contact contactOne;
	private Contact contactTwo;
	
	private List<Privilege> privileges;
	
	private Charge chargeOne;
	private Charge chargeTwo;
	private Charge chargeThree;
	
	private ReservationCharge reservationChargeOne = new ReservationCharge();
	private ReservationCharge reservationChargeTwo = new ReservationCharge();

	@Before
	public void setup() {
		createAdminUser();
		
		role = new Role("receptionistRole", "desc", true);
		roleRepo.save(role);
		
		user = new User("receptionist", "bobalina", "bobalina", superAdmin);
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
		userService.saveRole(managerUserType);

		user = new User();
		user.setPassword("password");
		user.setUserName("username");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		userService.saveUser(user, superAdmin.getUserName());

		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		roomService.saveAmenityType(amenityTypeRoomBasic);

		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
		roomService.saveAmenity(pillow);

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);

		standardRoomOne = new Room(1, operational, roomTypeStandard, user);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomOne);
		
		standardRoomTwo = new Room(2, operational, roomTypeStandard, user);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomTwo);
		
		standardRoomThree = new Room(3, operational, roomTypeStandard, user);
		standardRoomThree.setName("Room 3");
		standardRoomThree.setDescription("The Best Room Description");
		standardRoomThree.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomThree);

		idOne = new Identification(IdType.IDCard, "oneIdNumber");
		idTwo = new Identification(IdType.DriversLicense, "twoIdNumber");

		bookingService.createIdentification(idOne);
		bookingService.createIdentification(idTwo);

		contactOne = new Contact("some address", "cz");
		contactTwo = new Contact("some address", "cz");

		bookingService.createContact(contactOne);
		bookingService.createContact(contactTwo);

		guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", contactOne, idOne);
		bookingService.createGuest(guestOne);
		
		reservationOne = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));

		roomService.saveRoomRate(roomRateOne);
		roomService.saveRoomRate(roomRateTwo);
		
		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateOne);
		roomRates.add(roomRateTwo);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		bookingService.realiseReservation(reservationOne);
		
		chargeOne = new Charge(Currency.CZK, 100, "chargeOne", "chargeOneDesc");
		chargeTwo = new Charge(Currency.CZK, 200, "chargeTwo", "chargeTwoDesc");
		chargeThree = new Charge(Currency.CZK, 300, "chargeThree", "chargeThreeDesc");
		
		reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		reservationChargeTwo = new ReservationCharge();
		reservationChargeTwo.setCharge(chargeTwo);
		reservationChargeTwo.setQuantity(1);
		reservationChargeTwo.setReservation(reservationOne);
	}
	
	@Test
	public void testGetOutstandingCharges() {
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		invoiceService.saveCharge(chargeThree);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		//Two unpaid charges for the given reservation.
		assertEquals(2, invoiceService.getOutstandingCharges(reservationOne).size());
		
		Payment payment = new Payment();
		payment.setReservation(reservationOne);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne));
		payment.setPaymentType(PaymentType.Cash);
		invoiceService.savePayment(payment);
		
		//1 unpaid charge and 1 paid charge for the given reservation.
		assertEquals(1, invoiceService.getOutstandingCharges(reservationOne).size());
	}
	
	@Test
	public void testGetAllReservationChargesForReservation() {
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		invoiceService.saveCharge(chargeThree);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		assertEquals(2, invoiceService.getAllReservationChargesForAReservation(reservationOne).size());
	}
	
	@Test
	public void testGetAllPaymentsForReservation() {
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		invoiceService.saveCharge(chargeThree);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		Payment payment = new Payment();
		payment.setReservation(reservationOne);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne));
		payment.setPaymentType(PaymentType.Cash);
		invoiceService.savePayment(payment);
		
		assertEquals(1, invoiceService.getAllPaymentsForReservation(reservationOne).size());
	}
	
	@Test
	public void testCRUDCharge() {
		Charge chargeOne = new Charge(Currency.CZK, 100, "chargeOne", "chargeOneDesc");
		invoiceService.saveCharge(chargeOne);
		
		assertEquals(1, invoiceService.getAllCharges().size());
		
		chargeOne.setCurrency(Currency.USD);
		assertEquals(Currency.USD, invoiceService.getAllCharges().get(0).getCurrency());
		
		assertEquals(chargeOne, invoiceService.getChargeById(chargeOne.getId()));
		
		invoiceService.deleteCharge(chargeOne);
		assertEquals(0, invoiceService.getAllCharges().size());
	}
	
	@Test
	public void testCRUDReservationCharge() {
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveReservationCharge(reservationChargeOne);
		
		assertEquals(1, invoiceService.getAllReservationCharges().size());
		
		reservationChargeOne.setQuantity(100);
		assertEquals(100, invoiceService.getAllReservationCharges().get(0).getQuantity());
		
		assertEquals(reservationChargeOne, invoiceService.getReservationChargeById(reservationChargeOne.getId()));
		
		invoiceService.deleteReservationCharge(reservationChargeOne);
		assertEquals(0, invoiceService.getAllReservationCharges().size());
	}
	
	@Test
	public void testCRUDPayment() {
		Payment payment = new Payment();
		payment.setPaymentType(PaymentType.Cash);
		payment.setReservation(reservationOne);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne));
		invoiceService.savePayment(payment);
		
		assertEquals(1, invoiceService.getAllPayments().size());
		
		payment.setPaymentType(PaymentType.Cash);
		assertEquals(PaymentType.Cash, invoiceService.getAllPayments().get(0).getPaymentType());
		
		assertEquals(payment, invoiceService.getPaymentById(payment.getId()));
		
		invoiceService.deletePayment(payment);
		assertEquals(0, invoiceService.getAllPayments().size());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentGuest() {
		invoiceService.getChargeById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentContact() {
		invoiceService.getReservationChargeById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentIdentification() {
		invoiceService.getPaymentById(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentCharge() {
		invoiceService.deleteCharge(new Charge());
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentReservationCharge() {
		invoiceService.deleteReservationCharge(new ReservationCharge());
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentPayment() {
		invoiceService.deletePayment(new Payment());
	}
	
	@Test
	public void testSaveReservationWithInvalidReservationStatus() {
		reservationOne = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 6));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 5));

		roomService.saveRoomRate(roomRateOne);
		roomService.saveRoomRate(roomRateTwo);
		
		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateOne);
		roomRates.add(roomRateTwo);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		ReservationCharge chargeOne = new ReservationCharge();
		chargeOne.setReservation(reservationOne);
		
		try {
			invoiceService.saveReservationCharge(chargeOne);
			fail();
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testAreAllChargesPaidFor_NoCharges() {
		reservationOne = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 6));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 5));

		roomService.saveRoomRate(roomRateOne);
		roomService.saveRoomRate(roomRateTwo);
		
		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateOne);
		roomRates.add(roomRateTwo);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		assertTrue(invoiceService.areAllChargesPaidFor(reservationOne));
	}
	
	@Test
	public void testGetTotalOfOutstandingCharges() {
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		invoiceService.saveCharge(chargeThree);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		Reservation reservationTwo = new Reservation();
		reservationTwo.setFirstName("firstName");
		reservationTwo.setLastName("lastName");
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);
		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 6));
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 8));
		reservationTwo.setRoomRates(new ArrayList<RoomRate>());
		
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 6));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 7));

		roomService.saveRoomRate(roomRateThree);
		roomService.saveRoomRate(roomRateFour);
		
		List<RoomRate> roomRatesTwo = new ArrayList<RoomRate>();
		roomRatesTwo.add(roomRateThree);
		roomRatesTwo.add(roomRateFour);
		reservationTwo.setRoomRates(roomRatesTwo);
		
		bookingService.saveReservation(reservationTwo);
		bookingService.realiseReservation(reservationTwo);
		
		ReservationCharge rc1 = new ReservationCharge();
		rc1.setCharge(chargeOne);
		rc1.setQuantity(1);
		rc1.setReservation(reservationTwo);
		
		ReservationCharge rc2 = new ReservationCharge();
		rc2.setCharge(chargeTwo);
		rc2.setQuantity(1);
		rc2.setReservation(reservationTwo);
		
		invoiceService.saveReservationCharge(rc1);
		invoiceService.saveReservationCharge(rc2);
		
		List<Reservation> reservationsInProgress = new ArrayList<Reservation>();
		reservationsInProgress.add(reservationOne);
		reservationsInProgress.add(reservationTwo);
		
		assertEquals(600, invoiceService.getTotalOfOutstandingCharges(reservationsInProgress));
	}
}