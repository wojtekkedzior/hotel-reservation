package hotelreservation.model;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ModelEqualsTest {

	private User admin;
	private Role adminRole;
	private User manager;
	private Role managerRole;

	@Before
	public void setup() {
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
	}

	@Test
	public void testContact() {
		EqualsVerifier.forClass(Contact.class).verify();
	}

	@Test
	public void testAmenityType() {
		EqualsVerifier.forClass(AmenityType.class).verify();
	}

	@Test
	public void testAmenity() {
		EqualsVerifier.forClass(Amenity.class).verify();
	}

	@Test
	public void testCharge() {
		EqualsVerifier.forClass(Charge.class).verify();
	}

	@Test
	public void testGuest() {
		EqualsVerifier.forClass(Guest.class).verify();
	}

	@Test
	public void testIdentification() {
		EqualsVerifier.forClass(Identification.class).verify();
	}

	@Test
	public void testPrivilege() {
		EqualsVerifier.forClass(Privilege.class).verify();
	}

	@Test
	public void testStay() {
		EqualsVerifier.forClass(Stay.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testStatus() {
		EqualsVerifier.forClass(Status.class).verify();
	}

	@Test
	public void testUser() {
		EqualsVerifier.forClass(User.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testRoomType() {
		EqualsVerifier.forClass(RoomType.class).verify();
	}

	@Test
	public void testRoom() {
		EqualsVerifier.forClass(Room.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testRoomRate() {
		EqualsVerifier.forClass(RoomRate.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testReservation() {
		EqualsVerifier.forClass(Reservation.class).withPrefabValues(User.class, admin, manager).withIgnoredFields("occupants", "roomRates", "discountAuthorisedBy").verify();
	}

	@Test
	public void testReservationCancellation() {
		EqualsVerifier.forClass(ReservationCancellation.class).withPrefabValues(User.class, admin, manager).verify();

	}

	@Test
	public void testReservationCharge() {
		EqualsVerifier.forClass(ReservationCharge.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testReservationCheckout() {
		EqualsVerifier.forClass(ReservationCheckout.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testRole() {
		EqualsVerifier.forClass(Role.class).verify();
	}
}