package hotelreservation.model.ui;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;

public class UiModelEqualsTest {

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
		admin.setRole(adminRole);
		admin.setEnabled(true);

		manager = new User();
		manager.setPassword("password");
		manager.setFirstName("Manager");
		manager.setLastName("Manager");
		manager.setUserName("manager");
		manager.setEnabled(true);
		manager.setRole(managerRole);
	}
	
	@Test
	public void testPaymentDTO() {
		EqualsVerifier.forClass(PaymentDTO.class).withPrefabValues(User.class, admin, manager).verify();
	}
	
	@Test
	public void testReservationChargeDTO() {
		EqualsVerifier.forClass(ReservationChargeDTO.class) .verify();
	}

	@Test
	public void testUserDTO() {
		EqualsVerifier.forClass(UserDTO.class) .verify();
	}

	@Test
	public void testAmenityDTO() {
		EqualsVerifier.forClass(AmenityDTO.class) .verify();
	}

	@Test
	public void testAmenityTypeDTO() {
		EqualsVerifier.forClass(AmenityTypeDTO.class) .verify();
	}

	@Test
	public void testRoomRateDTO() {
		EqualsVerifier.forClass(RoomRateDTO.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testRoomTypeDTO() {
		EqualsVerifier.forClass(RoomTypeDTO.class).verify();
	}

	@Test
	public void testRoomDTO() {
		EqualsVerifier.forClass(RoomDTO.class).withPrefabValues(User.class, admin, manager).verify();
	}

	@Test
	public void testGuestDTO() {
		EqualsVerifier.forClass(GuestDTO.class).verify();
	}

	@Test
	public void testReservationCancellationDTO() {
		EqualsVerifier.forClass(ReservationCancellationDTO.class).withPrefabValues(User.class, admin, manager).verify();
	}
}