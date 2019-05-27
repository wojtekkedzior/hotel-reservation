package hotelreservation.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VerifyHashAndEqualsTest {
	
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
		//EqualsVerifier.forClass(Stay.class) .verify();
	}
	
	@Test
	public void testStatus() {
		EqualsVerifier.forClass(Status.class) .verify();
	}

	@Test
	public void testUser() {
		//EqualsVerifier.forClass(User.class) .verify();
	}
	
	@Test
	public void testRoomType() {
		EqualsVerifier.forClass(RoomType.class) .verify();
	}
	
	@Test
	public void testRoom() {
//		EqualsVerifier.forClass(Room.class) .verify();
	}
	
	@Test
	public void testRoomRate() {
//		EqualsVerifier.forClass(RoomRate.class) .verify();
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