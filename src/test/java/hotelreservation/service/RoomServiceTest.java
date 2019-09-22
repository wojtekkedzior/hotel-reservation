package hotelreservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import hotelreservation.exceptions.MissingOrInvalidArgumentException;
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
import hotelreservation.model.Role;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoomServiceTest extends BaseServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;
	
	private RoomType roomType;
	private Role managerUserType;
	private User createdBy;
	private Room room;
	private Status status;
	
	private AmenityType amenityTypeRoomBasic;
	private Amenity pillow;
	private Status operational;
	private RoomType roomTypeStandard;
	
	private Room standardRoomOne;
	private Room standardRoomTwo;
	
	@Before
	public void setup() {
		createAdminUser();
		
		roomType = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomType);

		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		createdBy = User.builder().userName("receptionist").password("password").firstName("firstName").lastName("lastName").createdBy(superAdmin).build();
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
		room.setCreatedOn(LocalDateTime.now());
		roomService.saveRoom(room);
	}

	@Test()
	public void testCRUDRoom() {
		Room createdRoom = roomService.getRoomById(room.getId());
		assertEquals(room, createdRoom);

		createdRoom.setName("New Best Room");
		createdRoom = roomService.getRoomById(room.getId());
		assertEquals("New Best Room", createdRoom.getName());

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
		roomService.deleteRoomType(roomType.getId());
		
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
		
		roomService.deleteAmenityType(amenityType.getId());
		assertTrue(roomService.getAllAmenityTypes().isEmpty());
	}
	
	@Test
	public void testCRUDRoomRate() {
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomService.saveRoomRate(roomRate);
		
		LocalDate startDate = LocalDate.of(2018, Month.JANUARY, 1);
		LocalDate endDate = LocalDate.of(2018, Month.JANUARY, 4);
		
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

	@Test(expected = Exception.class)
	public void testAddDuplicateRoomRate() {
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2017, Month.MARCH, 15));
		roomService.saveRoomRate(roomRate);
		assertEquals(1, roomService.getAllRoomRates().size());

		roomService.saveRoomRate( new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2017, Month.MARCH, 15)));
	}

	@Test
	public void testGetRoomByStatus() {
		assertEquals(1, roomService.getByRoomsByStatus(status).size());
	}

	@Test(expected = NotFoundException.class)
	public void testGetNonExistentAmenity() {
		roomService.getAmenityById(99);
	}

	@Test(expected = NotFoundException.class)
	public void testGetNonExistentAmenityType() {
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
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRoomTypeById() {
		roomService.deleteRoomType(99L);
	}
	@Test
	public void testGetRoomRatesAsMap() {
		setupRoomRates();

		Map<Room, List<RoomRate>> roomRatesAsMap = roomService.getRoomRatesAsMap(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));
		
		assertTrue(roomRatesAsMap.containsKey(standardRoomOne));
		assertTrue(roomRatesAsMap.containsKey(standardRoomTwo));
		
		assertEquals(3, roomRatesAsMap.get(standardRoomOne).size());
		assertEquals(3, roomRatesAsMap.get(standardRoomTwo).size());
	}
	
	@Test
	public void testGetRoomRates() {
		setupRoomRates();
		
		List<RoomRate> roomRates = roomService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));
		assertEquals(6, roomRates.size());
		
		roomRates = roomService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 5), LocalDate.of(2018, Month.JANUARY, 6));
		assertEquals(0, roomRates.size());
	}
	
	@Test
	public void testGetRoomRatesLastDayExcluded() {
		setupRoomRates();
		
		List<RoomRate> roomRates = roomService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));
		assertEquals(4, roomRates.size());
	}
	
	@Test
	public void testGetRoomRatesForSpecificRoom() {
		setupRoomRates();
		
		List<RoomRate> roomRates = roomService.getRoomRates(standardRoomOne, LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));
		assertEquals(3, roomRates.size());
		
		roomRates = roomService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 5), LocalDate.of(2018, Month.JANUARY, 6));
		assertEquals(0, roomRates.size());
	}
	
	@Test
	public void testGetRoomRatesPerDateSymmetrical() {
		setupRoomRates();
		
		LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 5);
		
		Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomService.getRoomRatesPerDate(start, end);
		
		assertEquals(3, roomRatesPerDate.size());
		assertEquals(2, roomRatesPerDate.get(start).size());
		assertEquals(2, roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3)).size());
		assertEquals(2, roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 4)).size());
		assertFalse(roomRatesPerDate.containsKey(end));
	}
	
	/**
	 *     roomOne   roomTwo
	 * 2     						x			
	 * 3     x				    x
	 */
	@Test
	public void testGetRoomRatesPerDateFirstRoomRateEmpty() {
		saveRooms();
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate1);
		
		RoomRate roomRate2 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomService.saveRoomRate(roomRate2);
		RoomRate roomRate3 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate3);
		
		LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);
		
		Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomService.getRoomRatesPerDate(start, end);
		
		assertEquals(2, roomRatesPerDate.size());
		
		List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
		assertEquals(2, roomRatesForJan2.size());
		assertNull(roomRatesForJan2.get(0));
		assertEquals(roomRate2, roomRatesForJan2.get(1));
		
		List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
		assertEquals(2, roomRatesForJan3.size());
		assertEquals(roomRate1, roomRatesForJan3.get(0));
		assertEquals(roomRate3, roomRatesForJan3.get(1));
		
		assertFalse(roomRatesPerDate.containsKey(end));
	}
	
	/**
	 *     roomOne   roomTwo
	 * 2     x								
	 * 3     x				    x
	 */
	@Test
	public void testGetRoomRatesPerDateLastRoomRateEmpty() {
		saveRooms();
		
		RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomService.saveRoomRate(roomRate1);
		RoomRate roomRate2 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate2);
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2))));
		RoomRate roomRate3 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate3);
	
		LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);
		
		Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomService.getRoomRatesPerDate(start, end);
		
		assertEquals(2, roomRatesPerDate.size());
		
		List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
		assertEquals(2, roomRatesForJan2.size());
		assertEquals(roomRate1, roomRatesForJan2.get(0));
		assertNull(roomRatesForJan2.get(1));
		
		List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
		assertEquals(2, roomRatesForJan3.size());
		assertEquals(roomRate2, roomRatesForJan3.get(0));
		assertEquals(roomRate3, roomRatesForJan3.get(1));
		
		assertFalse(roomRatesPerDate.containsKey(end));
	}
	
	/**
	 *     roomOne   roomTwo  roomThree
	 * 2     	 x								x
	 * 3       x                x             x
	 */
	@Test
	public void testGetRoomRatesPerDateMiddleRoomRateEmpty() {
		saveRooms();
		
		// add another room here as the 'middle' should be middle of the rooms and not dates
		Room standardRoomThree = new Room(3, operational, roomTypeStandard, createdBy);
		standardRoomThree.setName("Room 1");
		standardRoomThree.setDescription("The Best Room Description");
		standardRoomThree.setRoomAmenities(Collections.singletonList(pillow));
		roomService.saveRoom(standardRoomThree);
		
		RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomService.saveRoomRate(roomRate1);
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2))));
		RoomRate roomRate2 = new RoomRate(standardRoomThree, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
		roomService.saveRoomRate(roomRate2);
		
		RoomRate roomRate3 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate3);
		RoomRate roomRate4 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate4);
		RoomRate roomRate5 = new RoomRate(standardRoomThree, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
		roomService.saveRoomRate(roomRate5);
		
		LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);
		
		Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomService.getRoomRatesPerDate(start, end);
		
		assertEquals(2, roomRatesPerDate.size());
		
		List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
		assertEquals(3, roomRatesForJan2.size());
		assertEquals(roomRate1, roomRatesForJan2.get(0));
		assertNull(roomRatesForJan2.get(1));
		assertEquals(roomRate2, roomRatesForJan2.get(2));
		
		List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
		assertEquals(3, roomRatesForJan3.size());
		assertEquals(roomRate3, roomRatesForJan3.get(0));
		assertEquals(roomRate4, roomRatesForJan3.get(1));
		assertEquals(roomRate5, roomRatesForJan3.get(2));
		
		assertFalse(roomRatesPerDate.containsKey(end));
	}
	
	@Test
	public void testGetRoomRatesPerDateNoneAvailable() {
		saveRooms();
		
		LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);
		
		Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomService.getRoomRatesPerDate(start, end);
		assertEquals(0, roomRatesPerDate.size());
	}

	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testGetRoomRatesEndBeforeStart() {
		saveRooms();

		LocalDate start = LocalDate.of(2018, Month.JANUARY, 4);
		LocalDate end = LocalDate.of(2018, Month.JANUARY, 2);

		roomService.getRoomRatesPerDate(start, end);
	}

	private void saveRooms() {
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
		
		standardRoomOne = new Room(1, operational, roomTypeStandard, createdBy);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		standardRoomOne.setRoomAmenities(Collections.singletonList(pillow));
		roomService.saveRoom(standardRoomOne);
		
		standardRoomTwo = new Room(2, operational, roomTypeStandard, createdBy);
		standardRoomTwo.setName("Room 2");
		standardRoomTwo.setDescription("The Best Room Description");
		standardRoomTwo.setRoomAmenities(Collections.singletonList(pillow));
		roomService.saveRoom(standardRoomTwo);
	}
	
	private void setupRoomRates() {
		saveRooms();
	
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3)));
		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4)));
		
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3)));
		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4)));
	}
}