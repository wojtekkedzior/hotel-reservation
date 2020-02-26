package hotelreservation.service;

import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Before
	public void setup() {
		createAdminUser();
		
		roomType = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomType);

		managerUserType = new Role("manager", "manager desc", true);
		userService.saveRole(managerUserType);

		createdBy = User.builder().userName("receptionist").password("password").firstName("firstName").lastName("lastName").role(managerUserType).createdBy(superAdmin).build();
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
	public void testDeleteNonExistentStatus() {
		roomService.deleteStatus(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRoomTypeById() {
		roomService.deleteRoomType(99L);
	}
}