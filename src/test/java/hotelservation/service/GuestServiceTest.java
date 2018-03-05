package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
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
		Contact saveContact = guestService.saveContact(contact);
		assertEquals(saveContact, guestService.findContact(contact.getId()));
		
		saveContact.setCountry("new Country");
		guestService.saveContact(saveContact);
		assertEquals(saveContact, guestService.findContact(contact.getId()));
		
		guestService.deleteContact(new Long(contact.getId()).intValue());
		assertNull(guestService.findContact(contact.getId()));
	}
	
	
	@Test
	public void testCRUDIdentity() {
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.findIdentification(identification.getId()));
		
		identification.setIdType(IdType.Passport);
		guestService.saveIdentification(identification);
		assertEquals(identification, guestService.findIdentification(identification.getId()));
		
		guestService.deleteIdentification(Optional.of(new Long(identification.getId()).intValue()));
		assertNull(guestService.findIdentification(identification.getId()));
	}
	
	@Test
	public void testCRUDGuest() {
		guest.setFirstName("guestFirstName");
		guest.setLastName("guestLastName");
		
		guestService.saveGuest(guest);
		assertEquals(guest, guestService.findGuest(guest.getId()));
		
		guest.setFirstName("bob");
		guestService.saveGuest(guest);
		assertEquals(guest, guestService.findGuest(guest.getId()));
		
		guestService.deleteGuest(Optional.of(new Long(guest.getId()).intValue()));
		assertNull(guestService.findGuest(guest.getId()));
	}
	
}
