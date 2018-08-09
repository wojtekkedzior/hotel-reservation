package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
import hotelreservation.Utils;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Role;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class RoomServiceTest extends BaseServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private Utils dateConvertor;
	
	private RoomType roomType;
	private Role managerUserType;
	private User createdBy;
	private Room room;
	private Status status;
	
	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private RoomType roomTypeStandard;
	
	@Before
	public void setup() {
		createAdminUser();
		
		roomType = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomType);

		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		createdBy = new User();
		createdBy.setPassword("password");
		createdBy.setUserName("username");
		userService.saveUser(createdBy, superAdmin.getUserName());
		
		status = new Status("Status name", "Status Description");
		roomService.saveStatus(status);
		
		room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		room.setCreatedBy(createdBy);
		room.setCreatedOn(new Date());
		roomService.saveRoom(room);
	}

	@Test()
	public void testCRUDRoom() {
		Room createdRoom = roomService.getRoomById(room.getId());
		assertEquals(room, createdRoom);

		createdRoom.setName("New Best Room");
		createdRoom = roomService.getRoomById(room.getId());
		assertEquals(createdRoom.getName(), "New Best Room");

		roomService.deleteRoom(createdRoom);
		
		assertTrue(roomService.getAllRooms().isEmpty());
	}
	
	@Test
	public void testCRUDRoomType() {
		assertEquals(1, roomService.getAllRoomTypes().size());
		assertEquals(roomType, roomService.getAllRoomTypes().get(0));

		roomType.setName("Fancy");
		roomType.setDescription("Fancy room");

		RoomType updatedRoomType = roomService.getRoomTypeById(roomType.getId());
		assertEquals(roomType, updatedRoomType);

		//TODO something odd here with the contraint as if you delete the roomtype it's ok untill you try to retrive all room types
		
		roomService.deleteRoomById(room.getId());
		roomService.deleteRoomType(roomType);
		
		assertTrue(roomService.getAllRoomTypes().isEmpty());
	}
	
	@Test
	public void testCRUDStatus() {
		Status status = new Status("Status name", "Status Description");
		roomService.saveStatus(status);

		assertEquals(2, roomService.getAllStatuses().size());
		assertEquals(status, roomService.getStatusById(status.getId()));
		
		status.setName("Other Status");
		
		Status updatedStatus = roomService.getStatusById(status.getId());
		assertEquals(updatedStatus, status);
		
		roomService.deleteStatus(status.getId());
		assertEquals(1, roomService.getAllStatuses().size());
	}
	
	@Test
	public void testCRUDAmenity() {
		AmenityType standard = new AmenityType("standard", "standard");
		roomService.saveAmenityType(standard);
		
		Amenity amenity = new Amenity("pillow", "extra fluffy", standard);
		roomService.saveAmenity(amenity);

		assertEquals(1, roomService.getAllAmenities().size());
		assertEquals(amenity, roomService.getAllAmenities().get(0));

		amenity.setName("Fancy");
		amenity.setDescription("Fancy");

		Amenity updatedAmenity = roomService.getAmenityById(amenity.getId());
		assertEquals(amenity, updatedAmenity);

		roomService.deleteAmenity(amenity.getId());
		assertTrue(roomService.getAllAmenities().isEmpty());
	}
	
	@Test
	public void testCRUDAmenityType() {
		AmenityType amenityType = new AmenityType("standard", "standard");
		roomService.saveAmenityType(amenityType);
		
		assertEquals(1, roomService.getAllAmenityTypes().size());
		assertEquals(amenityType, roomService.getAllAmenityTypes().get(0));

		amenityType.setName("Fancy");
		amenityType.setDescription("Fancy");

		AmenityType updatedAmenityType = roomService.getAmenityTypeById(amenityType.getId());
		assertEquals(amenityType, updatedAmenityType);
		
		System.err.println(roomService.getAllAmenityTypes());

		roomService.deleteAmenityType(amenityType.getId());
		assertTrue(roomService.getAllAmenityTypes().isEmpty());
	}
	
	@Test
	public void testCRUDRoomRate() {
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		roomService.saveRoomRate(roomRate);
		
		Date startDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1));
		Date endDate = dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4));
		
		assertEquals(1, roomService.getRoomRates(startDate, endDate).size());
		assertEquals(roomRate, roomService.getRoomRates(startDate, endDate).get(0));

		roomRate.setValue(500);
		roomRate.setDescription("Fancy");

		RoomRate updatedRoomRate = roomService.getRoomRateById(roomRate.getId());
		assertEquals(roomRate, updatedRoomRate);

		roomService.deleteRoomRate(roomRate.getId());
		assertTrue(roomService.getRoomRates(startDate, endDate).isEmpty());
	}
	
	@Test
	public void testCreateStatus() {
		assertEquals(1, roomService.getAllStatuses().size());
		assertEquals(status, roomService.getStatusById(status.getId()));

		status.setName("Fancy");
		status.setDescription("Fancy");

		Status updatedStatus = roomService.getStatusById(status.getId());
		assertEquals(status, updatedStatus);

		roomService.deleteRoomById(room.getId());
		roomService.deleteStatus(status.getId()); 
		assertEquals(0, roomService.getAllStatuses().size());
	}

	@Test
	public void testAddDuplicateRoomRate() {
		Date day = dateConvertor.asDate(LocalDate.of(2017, Month.MARCH, 15));
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, day);
		roomService.saveRoomRate(roomRate);

		assertTrue(roomService.getAllRoomRates().size() == 1);

		RoomRate roomRate1 = new RoomRate(room, Currency.CZK, 1000, day);

		try {
			roomService.saveRoomRate(roomRate1);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetRoomByStatus() {
		assertTrue(roomService.getByRoomsByStatus(status).size() == 1);
	}

	@Test(expected = NotFoundException.class)
	public void testGetNonExistantAmenity() {
		roomService.getAmenityById(99);
	}

	@Test(expected = NotFoundException.class)
	public void testGetNonExistantAmenityType() {
		roomService.getAmenityTypeById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentRoom() {
		roomService.getRoomById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentRoomType() {
		roomService.getRoomTypeById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentRoomRate() {
		roomService.getRoomRateById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentStatus() {
		roomService.getStatusById(99);
	}
	
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentAmenity() {
		roomService.deleteAmenity(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentAmenityType() {
		roomService.deleteAmenityType(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRoomById() {
		roomService.deleteRoomById(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRoom() {
		roomService.deleteRoom(new Room());
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRoomRate() {
		roomService.deleteRoomRate(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentStatus() {
		roomService.deleteStatus(99);
	}
	
	@Test
	public void testGetRoomRatesAsMap() {
		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		roomService.saveAmenityType(amenityTypeRoomBasic);

		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
		roomService.saveAmenity(pillow);

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);
		
		Room standardRoomOne = new Room(1, operational, roomTypeStandard, createdBy);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomOne);
		
		Room standardRoomTwo = new Room(2, operational, roomTypeStandard, createdBy);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomTwo);
	
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		Map<Room, List<RoomRate>> roomRatesAsMap = roomService.getRoomRatesAsMap(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		
		assertTrue(roomRatesAsMap.containsKey(standardRoomOne));
		assertTrue(roomRatesAsMap.containsKey(standardRoomTwo));
		
		assertEquals(3, roomRatesAsMap.get(standardRoomOne).size());
		assertEquals(3, roomRatesAsMap.get(standardRoomTwo).size());
	}
	
	@Test
	public void testGetRoomRates() {
		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		roomService.saveAmenityType(amenityTypeRoomBasic);

		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
		roomService.saveAmenity(pillow);

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);
		
		Room standardRoomOne = new Room(1, operational, roomTypeStandard, createdBy);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomOne);
		
		Room standardRoomTwo = new Room(2, operational, roomTypeStandard, createdBy);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomTwo);
	
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		List<RoomRate> roomRates = roomService.getRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		assertEquals(6, roomRates.size());
		
		roomRates = roomService.getRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 5)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		assertEquals(0, roomRates.size());
	}
	
	@Test
	public void testGetRoomRatesForSpecificRoom() {
		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
		roomService.saveAmenityType(amenityTypeRoomBasic);

		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
		roomService.saveAmenity(pillow);

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);

		operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);
		
		Room standardRoomOne = new Room(1, operational, roomTypeStandard, createdBy);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomOne);
		
		Room standardRoomTwo = new Room(2, operational, roomTypeStandard, createdBy);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Arrays.asList(pillow));
		roomService.saveRoom(standardRoomTwo);
	
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 3))));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 4))));
		
		List<RoomRate> roomRates = roomService.getRoomRates(standardRoomOne, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 1)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		assertEquals(3, roomRates.size());
		
		roomRates = roomService.getRoomRates(dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 5)), dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 6)));
		assertEquals(0, roomRates.size());
	}
}