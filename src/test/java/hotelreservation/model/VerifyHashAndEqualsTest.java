package hotelreservation.model;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VerifyHashAndEqualsTest {
	
	public Status operational;
	private RoomType roomTypeStandard;
	private User admin;
	private Role adminRole;
	private Amenity pillow;
	private Amenity blanket;
	private Amenity safebox;
	private Amenity phone;
	private AmenityType amenityTypeRoomBasic;
	private User manager;
	private Role managerRole;
	
	@Before
	public void setup() {
//		operational = new Status("Operational", "Room is in operation");
//		roomTypeStandard = new RoomType("Standard", "Standard room");
		
//		Room standardRoomOne = new Room(1, operational, roomTypeStandard, admin);
//		standardRoomOne.setName("Room 1");
//		standardRoomOne.setDescription("The Best Room Description");
//		standardRoomOne.setCreatedBy(admin);
//		List<Amenity> standardRoomOneAmenitites = new ArrayList<>();
//		standardRoomOneAmenitites.add(pillow);
//		standardRoomOneAmenitites.add(phone);
//		standardRoomOneAmenitites.add(blanket);
//		standardRoomOne.setRoomAmenities(standardRoomOneAmenitites);
//
//		Room standardRoomTwo = new Room(2, operational, roomTypeStandard, admin);
//		standardRoomTwo.setName("Room 2");
//		standardRoomTwo.setDescription("The Second Best Room Description");
//		standardRoomTwo.setCreatedBy(admin);
//		List<Amenity> standardRoomTwoAmenitites = new ArrayList<>();
//		standardRoomTwoAmenitites.add(pillow);
//		standardRoomTwoAmenitites.add(safebox);
//		standardRoomTwoAmenitites.add(blanket);
//		standardRoomTwo.setRoomAmenities(standardRoomTwoAmenitites);
		
		adminRole = new Role("admin", "admin desc", true);
		managerRole = new Role("manager", "manager desc", true);
		
		admin = new User();
		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setUserName("admin");
		admin.setPassword("password");
		admin.setRoles(Arrays.asList(adminRole));
		admin.setEnabled(true);
		
		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRoles(Arrays.asList(managerRole));
		
//		amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
//		
//		pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
//		phone = new Amenity("phone", "phone", amenityTypeRoomBasic);
//		blanket = new Amenity("blanket", "blanket", amenityTypeRoomBasic);
//		safebox = new Amenity("safebox", "safebox", amenityTypeRoomBasic);
	}
	
	@Test
	public void testContact() {
//		EqualsVerifier.forClass(RoomRate.class).withIgnoredFields("room")
//	    .withPrefabValues(Room.class, applicationStartup.standardRoomOne, new Room())
//	    .verify();
		
		EqualsVerifier.forClass(Contact.class) .verify();
	}
	
	@Test
	public void testAmenityType() {
		EqualsVerifier.forClass(AmenityType.class) .verify();
	}
	
	@Test
	public void testAmenity() {
		EqualsVerifier.forClass(Amenity.class) .verify();
	}
	
	@Test
	public void testCharge() {
		EqualsVerifier.forClass(Charge.class) .verify();
	}
	
	@Test
	public void testGuest() {
		EqualsVerifier.forClass(Guest.class) .verify();
	}
	
	@Test
	public void testIdentification() {
		EqualsVerifier.forClass(Identification.class) .verify();
	}
	
	@Test
	public void testPrivilege() {
		EqualsVerifier.forClass(Privilege.class) .verify();
	}
	
	@Test
	public void testStay() {
//		EqualsVerifier.forClass(RoomRate.class).withIgnoredFields("room")
//	    .withPrefabValues(Room.class, applicationStartup.standardRoomOne, new Room())
//	    .verify();
		
//		EqualsVerifier.forClass(Stay.class) .verify();
	}
	
	@Test
	public void testStatus() {
		EqualsVerifier.forClass(Status.class) .verify();
	}

	@Test
	public void testUser() {
		//EqualsVerifier.forClass(User.class) .verify();
		EqualsVerifier.forClass(User.class)
	    .withPrefabValues(User.class, admin, manager)
	    .verify();
	}
	
	@Test
	public void testRoomType() {
		EqualsVerifier.forClass(RoomType.class) .verify();
	}
	
	@Test
	public void testRoom() {
		EqualsVerifier.forClass(Room.class)
	    .withPrefabValues(User.class, admin, manager)
	    .verify();
	}
	
	@Test
	public void testRoomRate() {
		EqualsVerifier.forClass(RoomRate.class)
	    .withPrefabValues(User.class, admin, manager)
	    .verify();
	}
	
	@Test
	public void testReservation() {
//		EqualsVerifier.forClass(Reservation.class) .verify();
	}
	@Test
	public void testReservationCancellation() {
//		EqualsVerifier.forClass(ReservationCancellation.class) .verify();
		
	}
	@Test
	public void testReservationCharge() {
//		EqualsVerifier.forClass(ReservationCharge.class) .verify();
	}
	@Test
	public void testReservationCheckout() {
//		EqualsVerifier.forClass(ReservationCheckout.class) .verify();
	}
	
	@Test
	public void testRole() {
		EqualsVerifier.forClass(Role.class) .verify();
	}
}