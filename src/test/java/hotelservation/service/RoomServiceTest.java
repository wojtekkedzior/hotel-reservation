package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

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
public class RoomServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private DateConvertor dateConvertor;
	
	private RoomType roomType;
	private Role managerUserType;
	private User createdBy;
	private Room room;
	private Status status;
	
	@Before
	public void setup() {
		roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		managerUserType = new Role("manager", "manager desc", true);
		userService.createRole(managerUserType);

		createdBy = new User();
		userService.createUser(createdBy);
		
		status = new Status("Status name", "Status Description");
		roomService.createStatus(status);
		
		room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		room.setCreatedBy(createdBy);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);
	}

	@Test
	public void testCRUDRoom() {
		Room createdRoom = roomService.getRoomById(room.getId());
		assertEquals(room, createdRoom);

		createdRoom.setName("New Best Room");
		createdRoom = roomService.getRoomById(room.getId());
		assertEquals(createdRoom.getName(), "New Best Room");

		roomService.deleteRoom(createdRoom);
		assertNull(roomService.getRoomById(room.getId()));
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
		
		roomService.deleteRoom(room.getId());
		roomService.deleteRoomType(roomType);
		
		assertTrue(roomService.getAllRoomTypes().isEmpty());
	}
	
	@Test
	public void testCRUDStatus() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

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
		roomService.createAmenityType(standard);
		
		Amenity amenity = new Amenity("pillow", "extra fluffy", standard);
		roomService.createAmenity(amenity);

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
		roomService.createAmenityType(amenityType);
		
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
		roomService.createRoomRate(roomRate);
		
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

		roomService.deleteRoom(room.getId());
		roomService.deleteStatus(status.getId()); 
		assertEquals(0, roomService.getAllStatuses().size());
	}

	@Test
	public void testAddDuplicateRoomRate() {
		Date day = dateConvertor.asDate(LocalDate.of(2017, Month.MARCH, 15));
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, day);
		roomService.createRoomRate(roomRate);

		assertTrue(roomService.getAllRoomRates().size() == 1);

		RoomRate roomRate1 = new RoomRate(room, Currency.CZK, 1000, day);

		try {
			roomService.createRoomRate(roomRate1);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testGetRoomByStatus() {
		assertTrue(roomService.getByRoomsByStatus(status).size() == 1);
	}

	@Test
	public void testGetRoomStatus() {
		assertEquals(status, roomService.getRoomStatus(room));
	}
}