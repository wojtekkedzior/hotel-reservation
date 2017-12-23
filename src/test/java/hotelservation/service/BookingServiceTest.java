package hotelservation.service;

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
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.repository.ReservationRepo;
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

	private Room standardRoomOne;
	private RoomType roomTypeStandard;

	// Room Rates
	private RoomRate roomRateOne;
	private RoomRate roomRateTwo;
	private RoomRate roomRateThree;
	private RoomRate roomRateFour;
	private RoomRate roomRateFive;
	private RoomRate roomRateSix;
	private RoomRate roomRateSeven;
	private RoomRate roomRateEight;

	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private User user;
	private UserType managerUserType;

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

	@Autowired
	private DateConvertor dateConvertor;

	@Before
	public void setup() {
		managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		user = new User();
		user.setUserType(managerUserType);
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
		List<Amenity> standardRoomOneAmenitites = new ArrayList<>();
		standardRoomOneAmenitites.add(pillow);
		standardRoomOne.setRoomAmenities(standardRoomOneAmenitites);
		roomService.createRoom(standardRoomOne);

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

		idOne = new Identification("IdOne Name", "IdOne Description", IdType.IDCard);
		idTwo = new Identification("IdTwo Name", "IdTwo Description", IdType.DriversLicense);

		bookingService.createIdentification(idOne);
		bookingService.createIdentification(idTwo);

		contactOne = new Contact();
		contactTwo = new Contact();

		bookingService.createContact(contactOne);
		bookingService.createContact(contactTwo);

		guestOne = new Guest("GuestOne First Name", "GuestOne Last Name", "GuestOne Description", contactOne, idOne);
		mainGuest = new Guest("GuestTWo First Name", "GuestTwo Last Name", "GuestTwo Description", contactTwo, idTwo);

		bookingService.createGuest(guestOne);
		bookingService.createGuest(mainGuest);
	}

	@Test
	public void testCreateReservation_MissingMiddleDay() {
		reservationOne = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setCreatedBy(user);
		reservationOne.setStartDate(new Date());
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
	public void testCreateReservation() {
		// ...|..........|................ reservationOne
		// .|...|......................... reservationTwo
		// .........|.........|........... reservationThree
		// .|......................|...... reservationFour

		reservationOne = new Reservation();
		reservationTwo = new Reservation();

		reservationOne.setMainGuest(mainGuest);
		reservationOne.setRoomRates(Arrays.asList(roomRateOne));
		reservationOne.setCreatedBy(user);
		reservationOne.setStartDate(new Date());
		reservationOne.setReservationStatus(ReservationStatus.UpComing);

		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		RoomRate roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3)));
		RoomRate roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		RoomRate roomRateFive = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 5)));
		RoomRate roomRateSix = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));

		roomService.createRoomRate(roomRateTwo);
		roomService.createRoomRate(roomRateThree);
		roomService.createRoomRate(roomRateFour);
		roomService.createRoomRate(roomRateFive);
		roomService.createRoomRate(roomRateSix);

		reservationOne.setStartDate(dateConvertor.asDate(LocalDate.of(2017, Month.JANUARY, 4)));
		reservationOne.setEndDate(dateConvertor.asDate(LocalDate.of(2017, Month.JANUARY, 6)));

		reservationOne.setRoomRates(Arrays.asList(roomRateFour, roomRateFive, roomRateSix));

		reservationTwo.setStartDate(dateConvertor.asDate(LocalDate.of(2017, Month.JANUARY, 2)));
		reservationTwo.setEndDate(dateConvertor.asDate(LocalDate.of(2017, Month.JANUARY, 6)));
	}

}
