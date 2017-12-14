package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
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
import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.model.enums.Currency;
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
	
	private Room standardRoomOne;
	private RoomType roomTypeStandard;
	
	// Room Rates
	private RoomRate roomRateOne;
	private RoomRate roomRateTwo;
	private RoomRate roomRateThree;
	private RoomRate roomRateFour;
	private RoomRate roomRateFive;
	
	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private User user;
	UserType managerUserType;
	
	@Autowired 
	private DateConvertor dateConvertor;

	@Before
	public void setup() {
		managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);
		
		user = new User();
		user.setUserType(managerUserType);
		
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
		
		roomRateOne = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4)));
		roomRateThree = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 5)));
		roomRateFour = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 10)));
		roomRateFive = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 20)));

		roomService.createRoomRate(roomRateOne);
		roomService.createRoomRate(roomRateTwo); 
		roomService.createRoomRate(roomRateThree);
		roomService.createRoomRate(roomRateFour);
		roomService.createRoomRate(roomRateFive);
	}

	@Test
	public void testCreateReservation() {
		Reservation reservation = new Reservation();
		
		reservation.setCreatedBy(user);
		reservation.setRoomRates(Arrays.asList(roomRateOne));

		
		
		
		
//		Status status = new Status("Status name", "Status Description");
//		roomService.createStatus(status);
//
//		List<Status> target = roomService.getAllStatuses();
//		assertTrue(target.size() == 1);
//		assertEquals(status, target.get(0));
	}
	

}
