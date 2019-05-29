package hotelreservation.model.ui;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import nl.jqno.equalsverifier.EqualsVerifier;

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
	public void testPaymentDTO() {
		EqualsVerifier.forClass(PaymentDTO.class).withPrefabValues(User.class, admin, manager).verify();
	}
	
	@Test
	public void testReservationChargeDTO() {
		EqualsVerifier.forClass(ReservationChargeDTO.class) .verify();
	}

}