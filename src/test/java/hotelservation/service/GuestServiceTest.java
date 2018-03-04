package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
	}
	
	@Test
	public void testCRUDContact() {
		Contact saveContact = guestService.saveContact(contact);
		assertEquals(saveContact, guestService.findContact(contact.getId()));
		
		guestService.deleteContact(contact.getId());
		assertNull(guestService.findContact(contact.getId()));
	}
	
	
	
	@Test
	public void testCRUDIdentity() {
//		guestService.saveIdentification(identification);
	}
	
	
	@Test
	public void testCRUDGuest() {
//		guestService.saveGuest(guest);
	}
	
	
	
	
}
