package hotelreservation.service;

import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.enums.IdType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GuestServiceTest {
	
	@Autowired
	private GuestService guestService;
	
	private Identification identification;
	private Contact contact;
	private Guest guest;
	
	@Before
	public void setup() {
		contact = new Contact("address1", "country1");
		identification = new Identification(IdType.ID_CARD, "12345abcde");
		guest = new Guest();
		guest.setContact(contact);
		guest.setIdentification(identification);
	}
	
	@Test
	public void testCRUDContact() {
		guestService.saveContact(contact);
		assertEquals(contact, guestService.getContactById(contact.getId()));
		
		contact.setCountry("new Country");
		guestService.saveContact(contact);
		assertEquals(contact, guestService.getContactById(contact.getId()));
		
		guestService.deleteContact(Long.valueOf(contact.getId()).intValue());
		assertTrue(guestService.getAllContacts().isEmpty());
	}
	
	@Test
	public void testCRUDIdentity() {
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.getIdentificationById(identification.getId()));
		
		identification.setIdType(IdType.PASSPORT);
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.getIdentificationById(identification.getId()));
		
		guestService.deleteIdentification(identification.getId());
		assertTrue(guestService.getAllIdentifications().isEmpty());
	}
	
	@Test
	public void testCRUDGuest() {
		guestService.saveContact(contact);
		guestService.saveIdentification(identification);
		
		guest.setFirstName("guestFirstName");
		guest.setLastName("guestLastName");
		
		guestService.saveGuest(guest);
		assertEquals(guest, guestService.getGuestById(guest.getId()));
		
		guest.setFirstName("bob");
		guestService.saveGuest(guest);
		assertEquals(guest, guestService.getGuestById(guest.getId()));
		
		//TODO cascading delete
		guestService.saveContact(contact);
		guestService.saveIdentification(identification);
		
		guestService.deleteGuest(guest.getId());
		assertTrue(guestService.getAllGuests().isEmpty());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentGuest() {
		guestService.getGuestById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentContact() {
		guestService.getContactById(99L);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentIdentification() {
		guestService.getIdentificationById(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentGuest() {
		guestService.deleteGuest(99L);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentIdentification() {
		guestService.deleteIdentification((99L));
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentContact() {
		guestService.deleteContact(99);
	}
}