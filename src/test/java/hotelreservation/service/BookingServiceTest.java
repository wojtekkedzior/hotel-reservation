package hotelreservation.service;

import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.model.*;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.finance.Payment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookingServiceTest extends BaseServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	private Room standardRoomOne;
	private Room standardRoomTwo;
	private Room standardRoomThree;
	private RoomType roomTypeStandard;

	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private User user;
	private Role managerUserType;

	private Guest guestOne;

	private Identification idOne;
	private Identification idTwo;

	private Contact contactOne;
	private Contact contactTwo;

	private Reservation reservationOne;
	private Reservation reservationTwo;
	private Reservation reservationThree;
	private Reservation reservationFour;
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	private RoomRate roomRateTwo;
	private RoomRate roomRateThree;

	@Before
	public void setup() {
		createAdminUser();
		
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

		idOne = new Identification(IdType.ID_CARD, "oneIdNumber");
		idTwo = new Identification(IdType.DRIVERS_LICENSE, "twoIdNumber");

		bookingService.createIdentification(idOne);
		bookingService.createIdentification(idTwo);

		contactOne = new Contact("some address", "cz");
		contactTwo = new Contact("some address", "cz");

		bookingService.createContact(contactOne);
		bookingService.createContact(contactTwo);

		guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", contactOne, idOne);
		bookingService.createGuest(guestOne);
		
		startDate = LocalDate.of(2018, Month.JANUARY, 1);
		endDate = LocalDate.of(2018, Month.JANUARY, 31);
		
		reservationOne = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UP_COMING);
		
		roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
	}

	@Test
	public void testCreateReservation_MissingMiddleDay() {
		roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		// no rate for the last day
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));

		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateFour);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 5));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateFour));

		try {
			bookingService.saveReservation(reservationOne);
			fail("Should not be able to save a reservation with non-sequential room rate dates");
		} catch (Exception e) {

		}
	}

	@Test
	public void testCreateReservation_OverLapExistingReservation_OnEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);

		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		roomService.saveRoomRate(roomRateFour);

		reservationTwo = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationTwo.setRoomRates(Arrays.asList(roomRateThree, roomRateFour));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UP_COMING);

		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 3)); // day 3 overlaps
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		try {
			bookingService.saveReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateReservation_OverLapExistingReservation_OnStartDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);

		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 1));
		roomService.saveRoomRate(roomRateOne);

		reservationTwo = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationTwo.setRoomRates(Arrays.asList(roomRateOne, roomRateTwo));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UP_COMING);

		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 1)); // day 3 overlaps
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 2));
		
		try {
			bookingService.saveReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateReservation_OverLapWholeExistingReservation() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);

		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 1));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		roomService.saveRoomRate(roomRateOne);
		roomService.saveRoomRate(roomRateFour);

		reservationTwo = new Reservation();
		reservationOne.setFirstName("firstName");
		reservationOne.setLastName("lastName");
		reservationTwo.setRoomRates(Arrays.asList(roomRateOne, roomRateTwo, roomRateThree, roomRateFour));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UP_COMING);

		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 1));
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		try {
			bookingService.saveReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateManyReservations() {
		RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 1));
		RoomRate roomRate2 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		RoomRate roomRate3 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		RoomRate roomRate4 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		RoomRate roomRate5 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 5));
		RoomRate roomRate6 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 6));
		RoomRate roomRate7 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 7));
		RoomRate roomRate8 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 8));
		RoomRate roomRate9 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 9));

		RoomRate roomRate10 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 10));
		RoomRate roomRate11 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 11));
		RoomRate roomRate12 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 12));
		RoomRate roomRate13 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 13));
		RoomRate roomRate14 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 14));
		RoomRate roomRate15 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 15));
		RoomRate roomRate16 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 16));
		RoomRate roomRate17 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 17));
		RoomRate roomRate18 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 18));
		RoomRate roomRate19 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 19));

		RoomRate roomRate20 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 20));
		RoomRate roomRate21 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 21));
		RoomRate roomRate22 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 22));
		RoomRate roomRate23 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 23));
		RoomRate roomRate24 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 24));
		RoomRate roomRate25 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 25));
		RoomRate roomRate26 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 26));
		RoomRate roomRate27 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 27));
		RoomRate roomRate28 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 28));
		RoomRate roomRate29 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 29));

		RoomRate roomRate30 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 30));
		RoomRate roomRate31 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 31));

		roomService.saveRoomRate(roomRate1);
		roomService.saveRoomRate(roomRate2);
		roomService.saveRoomRate(roomRate3);
		roomService.saveRoomRate(roomRate4);
		roomService.saveRoomRate(roomRate5);
		roomService.saveRoomRate(roomRate6);
		roomService.saveRoomRate(roomRate7);
		roomService.saveRoomRate(roomRate8);
		roomService.saveRoomRate(roomRate9);

		roomService.saveRoomRate(roomRate10);
		roomService.saveRoomRate(roomRate11);
		roomService.saveRoomRate(roomRate12);
		roomService.saveRoomRate(roomRate13);
		roomService.saveRoomRate(roomRate14);
		roomService.saveRoomRate(roomRate15);
		roomService.saveRoomRate(roomRate16);
		roomService.saveRoomRate(roomRate17);
		roomService.saveRoomRate(roomRate18);
		roomService.saveRoomRate(roomRate19);

		roomService.saveRoomRate(roomRate20);
		roomService.saveRoomRate(roomRate21);
		roomService.saveRoomRate(roomRate22);
		roomService.saveRoomRate(roomRate23);
		roomService.saveRoomRate(roomRate24);
		roomService.saveRoomRate(roomRate25);
		roomService.saveRoomRate(roomRate26);
		roomService.saveRoomRate(roomRate27);
		roomService.saveRoomRate(roomRate28);
		roomService.saveRoomRate(roomRate29);

		roomService.saveRoomRate(roomRate30);
		roomService.saveRoomRate(roomRate31);

		// ...|..........|................ reservationOne
		// ...............|...|........... reservationTwo
		// ....................|.........| reservationThree
		// |.|............................ reservationFour

		reservationTwo = new Reservation();
		reservationThree = new Reservation();
		reservationFour = new Reservation();

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 16));
		reservationOne.setRoomRates(
				Arrays.asList(roomRate4, roomRate5, roomRate6, roomRate7, roomRate8, roomRate9, roomRate10, roomRate11, roomRate12, roomRate13, roomRate14, roomRate15));

		assertEquals(31, roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.FEBRUARY, 1)).size());
		bookingService.saveReservation(reservationOne);
		assertEquals(19, roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.FEBRUARY, 1)).size());

		reservationTwo.setFirstName("firstName");
		reservationTwo.setLastName("lastName");
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UP_COMING);
		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 16));
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 21));
		reservationTwo.setRoomRates(Arrays.asList(roomRate16, roomRate17, roomRate18, roomRate19, roomRate20));

		bookingService.saveReservation(reservationTwo);
		assertEquals(14, roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.FEBRUARY, 1)).size());

		reservationThree.setFirstName("firstName");
		reservationThree.setLastName("lastName");
		reservationThree.setCreatedBy(user);
		reservationThree.setReservationStatus(ReservationStatus.UP_COMING);
		reservationThree.setStartDate(LocalDate.of(2018, Month.JANUARY, 21));
		reservationThree.setEndDate(LocalDate.of(2018, Month.FEBRUARY, 1));
		reservationThree
				.setRoomRates(Arrays.asList(roomRate21, roomRate22, roomRate23, roomRate24, roomRate25, roomRate26, roomRate27, roomRate28, roomRate29, roomRate30, roomRate31));

		bookingService.saveReservation(reservationThree);
		assertEquals(3, roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.FEBRUARY, 1)).size());

		reservationFour.setFirstName("firstName");
		reservationFour.setLastName("lastName");
		reservationFour.setCreatedBy(user);
		reservationFour.setReservationStatus(ReservationStatus.UP_COMING);
		reservationFour.setStartDate(LocalDate.of(2018, Month.JANUARY, 1));
		reservationFour.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationFour.setRoomRates(Arrays.asList(roomRate1, roomRate2, roomRate3));

		bookingService.saveReservation(reservationFour);
		assertEquals(0, roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 31)).size());
		assertEquals(4, bookingService.getAllReservations().size());
	}
	
	@Test
	public void testCreateManyReservationsOverMultipleRooms() {
		// ...|..........|................ reservationOne
		// .|...|......................... reservationTwo
		// .........|.........|........... reservationThree
		// .|......................|...... reservationFour
		
		reservationTwo = new Reservation();
		reservationThree = new Reservation();
		reservationFour = new Reservation();
 
		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 4));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 16));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		reservationTwo.setFirstName("firstName");
		reservationTwo.setLastName("lastName");
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UP_COMING);
		reservationTwo.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationTwo.setEndDate(LocalDate.of(2018, Month.JANUARY, 7));
		reservationTwo.setRoomRates(new ArrayList<RoomRate>());

		reservationThree.setFirstName("firstName");
		reservationThree.setLastName("lastName");
		reservationThree.setCreatedBy(user);
		reservationThree.setReservationStatus(ReservationStatus.UP_COMING);
		reservationThree.setStartDate(LocalDate.of(2018, Month.JANUARY, 10));
		reservationThree.setEndDate(LocalDate.of(2018, Month.JANUARY, 21));
		reservationThree.setRoomRates(new ArrayList<RoomRate>());
		
		reservationFour.setFirstName("firstName");
		reservationFour.setLastName("lastName");
		reservationFour.setCreatedBy(user);
		reservationFour.setReservationStatus(ReservationStatus.UP_COMING);
		reservationFour.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationFour.setEndDate(LocalDate.of(2018, Month.JANUARY, 26));
		reservationFour.setRoomRates(new ArrayList<RoomRate>());

		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, i));
			roomService.saveRoomRate(roomRate);

			if(i >= 4 && i <= 15 ) {
				reservationOne.getRoomRates().add(roomRate);
			} 
		}
		
		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, i));
			roomService.saveRoomRate(roomRate);

			if(i >= 2 && i <= 6 ) {
				reservationTwo.getRoomRates().add(roomRate);
			} 
			
			if(i >= 10 && i <= 20 ) {
				reservationThree.getRoomRates().add(roomRate);
			} 
		}
		
		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomThree, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, i));
			roomService.saveRoomRate(roomRate);

			if(i >= 2 && i <= 25 ) {
				reservationFour.getRoomRates().add(roomRate);
			} 
		}
		
		endDate = LocalDate.of(2018, Month.FEBRUARY, 1);
		
		assertEquals(93, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.saveReservation(reservationOne);
		
		assertEquals(81, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.saveReservation(reservationTwo);
		
		assertEquals(76, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.saveReservation(reservationThree);
		
		assertEquals(65, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.saveReservation(reservationFour);

		assertEquals(41, roomService.getAvailableRoomRates(startDate, endDate).size());
		assertEquals(4, bookingService.getAllReservations().size());
	}
	
	@Test
	public void testFindAvailableRoomRates() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));
		reservationOne.setRoomRates(Arrays.asList(roomRateTwo));

		LocalDate startDate = LocalDate.of(2018, Month.JANUARY, 1);
		LocalDate endDate = LocalDate.of(2018, Month.JANUARY, 4);

		assertEquals(2, roomService.getAvailableRoomRates(startDate, endDate).size());

		bookingService.saveReservation(reservationOne);

		assertEquals(1, roomService.getAvailableRoomRates(startDate, endDate).size());
		assertTrue(roomService.getAvailableRoomRates(startDate, endDate).get(0).equals(roomRateThree));
	}
	
	@Test
	public void testDeleteReservation() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		assertTrue(bookingService.getAllReservations().size() == 1);
		
		bookingService.deleteReservation(reservationOne);
		assertTrue(bookingService.getAllReservations().isEmpty());
	}
	
	@Test
	public void testCancelReservationBeforeStartOfReservation() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		assertTrue(bookingService.getAllReservations().size() == 1);
		
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 3));

		if(availableRoomRates.contains(roomRateTwo) || availableRoomRates.contains(roomRateThree)) {
			fail();
		}
		
		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("canceled");
		cancellation.setReservation(reservationOne);
		
		bookingService.cancelReservation(cancellation);
		
		assertEquals(ReservationStatus.CANCELLED, reservationOne.getReservationStatus());
		availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));
		
		if(!availableRoomRates.contains(roomRateTwo) || !availableRoomRates.contains(roomRateThree)) {
			fail();
		}
	}
	
	@Test
	public void testCancelReservationMidway() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);

		assertTrue(bookingService.getAllReservations().size() == 1);

		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 3));

		if(availableRoomRates.contains(roomRateTwo) || availableRoomRates.contains(roomRateThree)) {
			fail();
		}

		bookingService.realiseReservation(reservationOne);

		assertEquals(ReservationStatus.IN_PROGRESS, bookingService.getReservation(reservationOne.getId()).getReservationStatus());


		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("canceled");
		cancellation.setReservation(reservationOne);

		bookingService.cancelReservation(cancellation);

		assertEquals(ReservationStatus.ABANDONED, reservationOne.getReservationStatus());
		availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));

		if(!availableRoomRates.contains(roomRateTwo) || !availableRoomRates.contains(roomRateThree)) {
			fail();  //TODO this is not correct
		}

	}

	@Test
	public void testUpdateReservationWithNoRoomRates() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		try {
			bookingService.saveReservation(reservationOne);
			fail();
		} catch(MissingOrInvalidArgumentException e) {}
	}
	
	@Test 
	public void testUpdateReservationWithDifferentRates() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		
		reservationOne.setRoomRates(roomRates);
		
		try {
			bookingService.saveReservation(reservationOne);
		} catch(MissingOrInvalidArgumentException e) {}
		
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));
		assertEquals(1, availableRoomRates.size());
	}
	
	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testCreateReservationWithNotEnoughRates() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 5));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
	}
	
	/**
	 * going from -||-  to  ||--  This should ensure that the roomrate for the 3rd is free
	 */
	@Test 
	public void testUpdateReservationWithChangingSameRoomRatesDifferentStartDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 1));
		roomService.saveRoomRate(roomRateOne);
		
		ArrayList<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateOne);
		roomRates.add(roomRateTwo);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 1));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));
		reservationOne.setRoomRates(roomRates);
		
		try {
			bookingService.saveReservation(reservationOne);
		} catch(MissingOrInvalidArgumentException e) {
			fail(e.toString());
		}
		
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 4));
		
		assertEquals(1, availableRoomRates.size());
		assertTrue(availableRoomRates.contains(roomRateThree));
	}
	
	/**
	 * going from -||-  to  --||  This should ensure that the roomrate for the 2nd is free
	 */
	@Test 
	public void testUpdateReservationWithChangingSameRoomRatesDifferentEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4));
		roomService.saveRoomRate(roomRateFour);
		
		ArrayList<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateThree);
		roomRates.add(roomRateFour);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 3));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 5));
		reservationOne.setRoomRates(roomRates);
		
		try {
			bookingService.saveReservation(reservationOne);
		} catch(MissingOrInvalidArgumentException e) {
			fail(e.toString());
		}
		
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));
		
		assertEquals(1, availableRoomRates.size());
		assertTrue(availableRoomRates.contains(roomRateTwo));
	}
	
	@Test 
	public void testUpdateReservationWithEmptyStartDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		reservationOne.setStartDate(null);
		
		try {
			bookingService.saveReservation(reservationOne);
			fail();
		} catch(MissingOrInvalidArgumentException e) {}
	}
	
	@Test 
	public void testUpdateReservationWithEmptyEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		reservationOne.setEndDate(null);
		
		try {
			bookingService.saveReservation(reservationOne);
			fail();
		} catch(MissingOrInvalidArgumentException e) {}
	}
	
	@Test 
	public void testUpdateReservationWithStartDateBeforeEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
		
		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 4));
		
		try {
			bookingService.saveReservation(reservationOne);
			fail();
		} catch(MissingOrInvalidArgumentException e) {}
	}
	
	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testEmptyStartDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(null);
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
	}
	
	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testEmptyEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(null);

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
	}
	
	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testStartDateBeforeEndDate() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 3));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 2));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testGetNonExistentReservation() {
		bookingService.getReservation(99L);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentReservation() {
		bookingService.deleteReservation(new Reservation());
	}
	
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testReservationFulfillmentWithoutID() {
		bookingService.fulfillReservation(null);
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testReservationFulfillmentMissingReservation() {
		bookingService.fulfillReservation(99L);
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testReservationFulfillmentWrongStatus() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.saveReservation(reservationOne);

		bookingService.fulfillReservation(reservationOne.getId());
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testReservationFulfillmentWithoutPaymentWithCharges() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));

		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		bookingService.realiseReservation(reservationOne);
		assertEquals(ReservationStatus.IN_PROGRESS, reservationOne.getReservationStatus());
		
		Charge chargeOne = new Charge(Currency.CZK, 100, "chargeOne", "chargeOneDesc");
		
		ReservationCharge reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveReservationCharge(reservationChargeOne);
		
		bookingService.fulfillReservation(reservationOne.getId());
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testReservationFulfillmentWithNotEnoughPayment() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 3));

		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		bookingService.realiseReservation(reservationOne);
		assertEquals(ReservationStatus.IN_PROGRESS, reservationOne.getReservationStatus());
		
		Charge chargeOne = new Charge(Currency.CZK, 100, "chargeOne", "chargeOneDesc");
		Charge chargeTwo = new Charge(Currency.CZK, 200, "chargeTwo", "chargeTwoDesc");
		
		ReservationCharge reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		ReservationCharge reservationChargeTwo = new ReservationCharge();
		reservationChargeTwo.setCharge(chargeTwo);
		reservationChargeTwo.setQuantity(1);
		reservationChargeTwo.setReservation(reservationOne);
		
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		Payment payment = new Payment();
		payment.setReservation(reservationOne);
		payment.setPaymentType(PaymentType.CASH);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne));
		invoiceService.savePayment(payment);
		
		bookingService.fulfillReservation(reservationOne.getId());
	}
	
	@Test
	public void testReservationFulfillmentWithNoCharges() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		bookingService.realiseReservation(reservationOne);
		
		assertEquals(ReservationStatus.IN_PROGRESS, reservationOne.getReservationStatus());
		bookingService.fulfillReservation(reservationOne.getId());
		assertEquals(ReservationStatus.FULFILLED, reservationOne.getReservationStatus());
	}
	
	@Test
	public void testReservationFulfillmentWithPaymentsForAllCharges() {
		roomService.saveRoomRate(roomRateTwo);
		roomService.saveRoomRate(roomRateThree);

		reservationOne.setStartDate(LocalDate.of(2018, Month.JANUARY, 2));
		reservationOne.setEndDate(LocalDate.of(2018, Month.JANUARY, 4));

		List<RoomRate> roomRates = new ArrayList<RoomRate>();
		roomRates.add(roomRateTwo);
		roomRates.add(roomRateThree);
		
		reservationOne.setRoomRates(roomRates);
		bookingService.saveReservation(reservationOne);
		
		bookingService.realiseReservation(reservationOne);
		assertEquals(ReservationStatus.IN_PROGRESS, reservationOne.getReservationStatus());
		
		Charge chargeOne = new Charge(Currency.CZK, 100, "chargeOne", "chargeOneDesc");
		Charge chargeTwo = new Charge(Currency.CZK, 200, "chargeTwo", "chargeTwoDesc");
		
		ReservationCharge reservationChargeOne = new ReservationCharge();
		reservationChargeOne.setCharge(chargeOne);
		reservationChargeOne.setQuantity(1);
		reservationChargeOne.setReservation(reservationOne);
		
		ReservationCharge reservationChargeTwo = new ReservationCharge();
		reservationChargeTwo.setCharge(chargeTwo);
		reservationChargeTwo.setQuantity(1);
		reservationChargeTwo.setReservation(reservationOne);
		
		invoiceService.saveCharge(chargeOne);
		invoiceService.saveCharge(chargeTwo);
		
		invoiceService.saveReservationCharge(reservationChargeOne);
		invoiceService.saveReservationCharge(reservationChargeTwo);
		
		Payment payment = new Payment();
		payment.setReservation(reservationOne);
		payment.setReservationCharges(Arrays.asList(reservationChargeOne, reservationChargeTwo));
		payment.setPaymentType(PaymentType.CASH);
		invoiceService.savePayment(payment);
		
		bookingService.fulfillReservation(reservationOne.getId());
		assertEquals(ReservationStatus.FULFILLED, reservationOne.getReservationStatus());
	}
	
	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testGetReservationWithEmptyId() {
		bookingService.getReservation(null);
	}
}