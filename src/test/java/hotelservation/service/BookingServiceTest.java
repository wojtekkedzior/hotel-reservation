package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
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
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.Role;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class BookingServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;
	
//	  @Autowired
//	    private TestRestTemplate restTemplate;
	
	private Room standardRoomOne;
	private Room standardRoomTwo;
	private Room standardRoomThree;
	private RoomType roomTypeStandard;

//	// Room Rates
//	private RoomRate roomRateOne;
//	private RoomRate roomRateTwo;
//	private RoomRate roomRateThree;
//	private RoomRate roomRateFour;
//	private RoomRate roomRateFive;
//	private RoomRate roomRateSix;
//	private RoomRate roomRateSeven;
//	private RoomRate roomRateEight;

	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private User user;
	private Role managerUserType;

	private Guest mainGuest;
	private Guest guestOne;

	private Identification idOne;
	private Identification idTwo;

	private Contact contactOne;
	private Contact contactTwo;

	private Reservation reservationOne;
	private Reservation reservationTwo;
	private Reservation reservationThree;
	private Reservation reservationFour;
	
	private Date startDate;
	private Date endDate;

	@Autowired
	private DateConvertor dateConvertor;

	@Before
	public void setup() {
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

		// roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		// roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		// roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		// roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 8)));
		// roomRateFive = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 10)));
		// roomRateSix = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		// roomRateSeven = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 20)));
		// roomRateEight = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 25)));
		//
		// roomService.createRoomRate(roomRateOne);
		// roomService.createRoomRate(roomRateTwo);
		// roomService.createRoomRate(roomRateThree);
		// roomService.createRoomRate(roomRateFour);
		// roomService.createRoomRate(roomRateFive);
		// roomService.createRoomRate(roomRateSix);
		// roomService.createRoomRate(roomRateSeven);
		// roomService.createRoomRate(roomRateEight);

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
	public void testCreateReservation_MissingMiddleDay() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		// no rate for the middle day
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateFour);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateFour));

		try {
			bookingService.createReservation(reservationOne);
			fail("Should not be able to save a reservation with non-sequential room rate dates");
		} catch (Exception e) {

		}
	}

	@Test
	public void testCreateReservation_OverLapExistingReservation_OnEndDate() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.createReservation(reservationOne);

		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		roomService.createRoomRate(roomRateFour);

		reservationTwo = new Reservation();
		reservationTwo.setMainGuest(mainGuest);
		reservationTwo.setRoomRates(Arrays.asList(roomRateThree, roomRateFour));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);

		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))); // day 3 overlaps
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		try {
			bookingService.createReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateReservation_OverLapExistingReservation_OnStartDate() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.createReservation(reservationOne);

		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));
		roomService.createRoomRate(roomRateOne);

		reservationTwo = new Reservation();
		reservationTwo.setMainGuest(mainGuest);
		reservationTwo.setRoomRates(Arrays.asList(roomRateOne, roomRateTwo));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);

		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1))); // day 3 overlaps
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));

		try {
			bookingService.createReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateReservation_OverLapWholeExistingReservation() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.createReservation(reservationOne);

		RoomRate roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		roomService.createRoomRate(roomRateOne);
		roomService.createRoomRate(roomRateFour);

		reservationTwo = new Reservation();
		reservationTwo.setMainGuest(mainGuest);
		reservationTwo.setRoomRates(Arrays.asList(roomRateOne, roomRateTwo, roomRateThree, roomRateFour));
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);

		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));

		try {
			bookingService.createReservation(reservationTwo);
			fail("No rooms available for the given day");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreateManyReservations() {
		RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));
		RoomRate roomRate2 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRate3 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		RoomRate roomRate4 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		RoomRate roomRate5 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 5)));
		RoomRate roomRate6 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		RoomRate roomRate7 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 7)));
		RoomRate roomRate8 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 8)));
		RoomRate roomRate9 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 9)));

		RoomRate roomRate10 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 10)));
		RoomRate roomRate11 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 11)));
		RoomRate roomRate12 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 12)));
		RoomRate roomRate13 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 13)));
		RoomRate roomRate14 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 14)));
		RoomRate roomRate15 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		RoomRate roomRate16 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 16)));
		RoomRate roomRate17 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 17)));
		RoomRate roomRate18 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 18)));
		RoomRate roomRate19 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 19)));

		RoomRate roomRate20 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 20)));
		RoomRate roomRate21 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 21)));
		RoomRate roomRate22 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 22)));
		RoomRate roomRate23 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 23)));
		RoomRate roomRate24 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 24)));
		RoomRate roomRate25 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 25)));
		RoomRate roomRate26 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 26)));
		RoomRate roomRate27 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 27)));
		RoomRate roomRate28 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 28)));
		RoomRate roomRate29 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 29)));

		RoomRate roomRate30 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 30)));
		RoomRate roomRate31 = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 31)));

		roomService.createRoomRate(roomRate1);
		roomService.createRoomRate(roomRate2);
		roomService.createRoomRate(roomRate3);
		roomService.createRoomRate(roomRate4);
		roomService.createRoomRate(roomRate5);
		roomService.createRoomRate(roomRate6);
		roomService.createRoomRate(roomRate7);
		roomService.createRoomRate(roomRate8);
		roomService.createRoomRate(roomRate9);

		roomService.createRoomRate(roomRate10);
		roomService.createRoomRate(roomRate11);
		roomService.createRoomRate(roomRate12);
		roomService.createRoomRate(roomRate13);
		roomService.createRoomRate(roomRate14);
		roomService.createRoomRate(roomRate15);
		roomService.createRoomRate(roomRate16);
		roomService.createRoomRate(roomRate17);
		roomService.createRoomRate(roomRate18);
		roomService.createRoomRate(roomRate19);

		roomService.createRoomRate(roomRate20);
		roomService.createRoomRate(roomRate21);
		roomService.createRoomRate(roomRate22);
		roomService.createRoomRate(roomRate23);
		roomService.createRoomRate(roomRate24);
		roomService.createRoomRate(roomRate25);
		roomService.createRoomRate(roomRate26);
		roomService.createRoomRate(roomRate27);
		roomService.createRoomRate(roomRate28);
		roomService.createRoomRate(roomRate29);

		roomService.createRoomRate(roomRate30);
		roomService.createRoomRate(roomRate31);

		// ...|..........|................ reservationOne
		// ...............|...|........... reservationTwo
		// ....................|.........| reservationThree
		// |.|............................ reservationFour

		reservationOne = new Reservation();
		reservationTwo = new Reservation();
		reservationThree = new Reservation();
		reservationFour = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		reservationOne.setRoomRates(
				Arrays.asList(roomRate4, roomRate5, roomRate6, roomRate7, roomRate8, roomRate9, roomRate10, roomRate11, roomRate12, roomRate13, roomRate14, roomRate15));

		assertEquals(31, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.createReservation(reservationOne);
		assertEquals(19, roomService.getAvailableRoomRates(startDate, endDate).size());

		reservationTwo.setMainGuest(mainGuest);
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);
		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 16)));
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 20)));
		reservationTwo.setRoomRates(Arrays.asList(roomRate16, roomRate17, roomRate18, roomRate19, roomRate20));

		bookingService.createReservation(reservationTwo);
		assertEquals(14, roomService.getAvailableRoomRates(startDate, endDate).size());

		reservationThree.setMainGuest(mainGuest);
		reservationThree.setCreatedBy(user);
		reservationThree.setReservationStatus(ReservationStatus.UpComing);
		reservationThree.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 21)));
		reservationThree.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 31)));
		reservationThree
				.setRoomRates(Arrays.asList(roomRate21, roomRate22, roomRate23, roomRate24, roomRate25, roomRate26, roomRate27, roomRate28, roomRate29, roomRate30, roomRate31));

		bookingService.createReservation(reservationThree);
		assertEquals(3, roomService.getAvailableRoomRates(startDate, endDate).size());

		reservationFour.setMainGuest(mainGuest);
		reservationFour.setCreatedBy(user);
		reservationFour.setReservationStatus(ReservationStatus.UpComing);
		reservationFour.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)));
		reservationFour.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		reservationFour.setRoomRates(Arrays.asList(roomRate1, roomRate2, roomRate3));

		bookingService.createReservation(reservationFour);
		assertEquals(0, roomService.getAvailableRoomRates(startDate, endDate).size());
		assertEquals(4, bookingService.getAllReservations().size());
	}
	
	@Test
	public void testCreateManyReservationsOverMultipleRooms() {
		// ...|..........|................ reservationOne
		// .|...|......................... reservationTwo
		// .........|.........|........... reservationThree
		// .|......................|...... reservationFour
		
		reservationOne = new Reservation();
		reservationTwo = new Reservation();
		reservationThree = new Reservation();
		reservationFour = new Reservation();
 
		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);
		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 15)));
		reservationOne.setRoomRates(new ArrayList<RoomRate>());
		
		reservationTwo.setMainGuest(mainGuest);
		reservationTwo.setCreatedBy(user);
		reservationTwo.setReservationStatus(ReservationStatus.UpComing);
		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		reservationTwo.setRoomRates(new ArrayList<RoomRate>());

		reservationThree.setMainGuest(mainGuest);
		reservationThree.setCreatedBy(user);
		reservationThree.setReservationStatus(ReservationStatus.UpComing);
		reservationThree.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 10)));
		reservationThree.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 20)));
		reservationThree.setRoomRates(new ArrayList<RoomRate>());
		
		reservationFour.setMainGuest(mainGuest);
		reservationFour.setCreatedBy(user);
		reservationFour.setReservationStatus(ReservationStatus.UpComing);
		reservationFour.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationFour.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 25)));
		reservationFour.setRoomRates(new ArrayList<RoomRate>());

		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, i)));
			roomService.createRoomRate(roomRate);

			if(i >= 4 && i <= 15 ) {
				reservationOne.getRoomRates().add(roomRate);
			} 
		}
		
		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, i)));
			roomService.createRoomRate(roomRate);

			if(i >= 2 && i <= 6 ) {
				reservationTwo.getRoomRates().add(roomRate);
			} 
			
			if(i >= 10 && i <= 20 ) {
				reservationThree.getRoomRates().add(roomRate);
			} 
		}
		
		for(int i = 1; i <= 31; i++) {
			RoomRate roomRate = new RoomRate(standardRoomThree, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, i)));
			roomService.createRoomRate(roomRate);

			if(i >= 2 && i <= 25 ) {
				reservationFour.getRoomRates().add(roomRate);
			} 
		}
		
		assertEquals(93, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.createReservation(reservationOne);
		
		assertEquals(81, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.createReservation(reservationTwo);
		
		assertEquals(76, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.createReservation(reservationThree);
		
		assertEquals(65, roomService.getAvailableRoomRates(startDate, endDate).size());
		bookingService.createReservation(reservationFour);

		assertEquals(41, roomService.getAvailableRoomRates(startDate, endDate).size());
		assertEquals(4, bookingService.getAllReservations().size());
	}
	
	@Test
	public void testFindAvailableRoomRates() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo));

		Date startDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1));
		Date endDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4));

		assertEquals(2, roomService.getAvailableRoomRates(startDate, endDate).size());

		bookingService.createReservation(reservationOne);

		assertEquals(1, roomService.getAvailableRoomRates(startDate, endDate).size());
		assertTrue(roomService.getAvailableRoomRates(startDate, endDate).get(0).equals(roomRateThree));
	}
	
	@Test
	public void testDeleteReservation() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.createReservation(reservationOne);
		
		assertTrue(bookingService.getAllReservations().size() == 1);
		
		bookingService.deleteReservation(reservationOne);
		assertTrue(bookingService.getAllReservations().isEmpty());
	}
	
	@Test
	public void testCancelReservation() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		reservationOne.setRoomRates(Arrays.asList(roomRateTwo, roomRateThree));
		bookingService.createReservation(reservationOne);
		
		assertTrue(bookingService.getAllReservations().size() == 1);
		
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));

		if(availableRoomRates.contains(roomRateTwo) || availableRoomRates.contains(roomRateThree)) {
			fail();
		}
		
		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("canceled");
		cancellation.setReservation(reservationOne);
		
		bookingService.cancelReservation(cancellation);
		
		assertEquals(ReservationStatus.Cancelled, reservationOne.getReservationStatus());
		availableRoomRates = roomService.getAvailableRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		
		if(!availableRoomRates.contains(roomRateTwo) || !availableRoomRates.contains(roomRateThree)) {
			fail();
		}
	}
	
	@Test
	public void testRealiseReservation() {
		
	}
	
	@Test
	public void testCancelReservationMidway() {
		
	}
}
