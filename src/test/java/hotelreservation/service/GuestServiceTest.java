package hotelreservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.enums.IdType;
import hotelreservation.service.GuestService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class GuestServiceTest {
	
	@Autowired
	private GuestService guestService;
	
	private Identification identification;
	private Contact contact;
	private Guest guest;
	
	@Before
	public void setup() {
		contact = new Contact("address1", "country1");
		identification = new Identification(IdType.IDCard, "12345abcde");
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
		
		guestService.deleteContact(new Long(contact.getId()).intValue());
		assertTrue(guestService.getAllContacts().isEmpty());
	}
	
	@Test
	public void testCRUDIdentity() {
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.getIdentificationById(identification.getId()));
		
		identification.setIdType(IdType.Passport);
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.getIdentificationById(identification.getId()));
		
		guestService.deleteIdentification(Optional.of(new Long(identification.getId()).intValue()));
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
		
		guestService.deleteGuest(Optional.of(new Long(guest.getId()).intValue()));
		assertTrue(guestService.getAllGuests().isEmpty());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentGuest() {
		guestService.getGuestById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentContact() {
		guestService.getContactById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentIdentification() {
		guestService.getIdentificationById(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentGuest() {
		guestService.deleteGuest(Optional.of(new Integer(99)));
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentIdentification() {
		guestService.deleteIdentification(Optional.of(new Integer(99)));
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentContact() {
		guestService.deleteContact(99);
	}
}