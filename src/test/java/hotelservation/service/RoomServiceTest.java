package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
public class RoomServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Before
	public void setup() {
		addStatuses();
	}

	private void addStatuses() {
		// TODO Auto-generated method stub

	}

	@Test
	public void testCreateStatus() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

		List<Status> target = roomService.getAllStatuses();
		assertTrue(target.size() == 1);
		assertEquals(status, target.get(0));
	}

	@Test
	public void testCrudRoomType() {
		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		// when
		List<RoomType> target = roomService.getAllRoomTypes();

		assertTrue(target.size() == 1);
		assertEquals(roomType, target.get(0));

		roomType.setName("Fancy");
		roomType.setDescription("Fancy room");

		RoomType updatedRoomType = roomService.getRoomTypeById(roomType.getId());
		assertEquals(roomType, updatedRoomType);

		roomService.deleteRoomType(roomType);

		target = roomService.getAllRoomTypes();
		assertTrue(target.size() == 0);
	}

	@Test
	public void testCrudRoom() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		UserType managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		User createdBy = new User();
		createdBy.setUserType(managerUserType);

		Room room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		room.setCreatedBy(createdBy);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);

		Room createdRoom = roomService.getRoomById(room.getId());
		assertEquals(room, createdRoom);

		createdRoom.setName("New Best Room");
		createdRoom = roomService.getRoomById(room.getId());
		assertEquals(createdRoom.getName(), "New Best Room");

		roomService.deleteRoom(createdRoom);
		assertNull(roomService.getRoomById(room.getId()));
	}

	@Test
	public void testAddAllUserTypes() {
	}

	@Test
	public void testAddDuplicateRoomRate() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		UserType managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		Room room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);

		Date day = new Date(2017, 3, 15);
		RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, day);
		roomService.createRoomRate(roomRate);

		assertTrue(roomService.getAvailableRoomRates().size() == 1);

		RoomRate roomRate1 = new RoomRate(room, Currency.CZK, 1000, day);

		try {
			roomService.createRoomRate(roomRate1);
			fail();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	@Test
	public void testGetRoomByStatus() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		UserType managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		User createdBy = new User();
		createdBy.setUserType(managerUserType);

		Room room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		// room.setCreatedBy(createdBy);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);

		assertTrue(roomService.getByRoomsByStatus(status).size() == 1);
	}

	@Test
	public void testGetRoomStatus() {
		Status status = new Status("Status name", "Status Description");
		roomService.createStatus(status);

		RoomType roomType = new RoomType("Standard", "Standard room");
		roomService.createRoomType(roomType);

		UserType managerUserType = new UserType("manager", "manager desc", true);
		userService.createUserType(managerUserType);

		User createdBy = new User();
		createdBy.setUserType(managerUserType);

		Room room = new Room();
		room.setRoomNumber(1);
		room.setName("The Best Room");
		room.setDescription("The Best Room Description");
		room.setStatus(status);
		room.setRoomType(roomType);
		// room.setCreatedBy(createdBy);
		room.setCreatedOn(new Date());
		roomService.createRoom(room);

		assertEquals(status, roomService.getRoomStatus(room));
	}
}