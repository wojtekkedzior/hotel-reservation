package hotelreservation.service;

import hotelreservation.model.*;
import hotelreservation.model.finance.Invoice;
import hotelreservation.model.finance.Payment;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("dev")
public class AuditTest {

    @Autowired
    private EntityManager entityManager;

    private AuditReader reader;

    @Before
    public void setup() {
        reader = AuditReaderFactory.get(entityManager);
    }

    @Test
    public void testUser() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(User.class, true);
        assertEquals(4, query.getResultList().size());
    }

    @Test
    public void testRoomRate() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(RoomRate.class, true);
        assertEquals(2190, query.getResultList().size());
    }

    @Test
    public void testPrivilege() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Privilege.class, true);
        assertEquals(23, query.getResultList().size());
    }

    @Test
    public void testIdentification() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Identification.class, true);
        assertEquals(4, query.getResultList().size());
    }

    @Test
    public void testGuest() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Guest.class, true);
        assertEquals(4, query.getResultList().size());
    }

    @Test
    public void testContact() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Contact.class, true);
        assertEquals(4, query.getResultList().size());
    }

    @Test
    public void testCharge() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Charge.class, true);
        assertEquals(3, query.getResultList().size());
    }
    @Test
    public void testAmenity() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Amenity.class, true);
        assertEquals(15, query.getResultList().size());
    }

    @Test
    public void testStay() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Stay.class, true);
        assertEquals(0, query.getResultList().size());
    }

    @Test
    public void testReservationCheckout() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(ReservationCheckout.class, true);
        assertEquals(0, query.getResultList().size());
    }

    @Test
    public void testInvoice() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Invoice.class, true);
        assertEquals(0, query.getResultList().size());
    }

    @Test
    public void testPayment() {
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Payment.class, true);
        assertEquals(0, query.getResultList().size());
    }
}
