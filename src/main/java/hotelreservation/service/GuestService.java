package hotelreservation.service;

import hotelreservation.Utils;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.repository.ContactRepo;
import hotelreservation.repository.GuestRepo;
import hotelreservation.repository.IdentificationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GuestService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GuestRepo guestRepo;

    @Autowired
    private IdentificationRepo identificationRepo;

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private Utils utils;

    public Contact saveContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public Guest saveGuest(Guest guest) {
        return guestRepo.save(guest);
    }

    public Identification saveIdentification(Identification identification) {
        return identificationRepo.save(identification);
    }

    public Guest getGuestById(long id) {
        log.info("Looking for Guest with ID: {}", id);
        return guestRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Contact getContactById(long id) {
        log.info("Looking for Contact with ID: {}", id);
        return contactRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Identification getIdentificationById(long id) {
        log.info("Looking for Identification with ID: {}", id);
        return identificationRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<Contact> getAllContacts() {
        return utils.toList(contactRepo.findAll());
    }

    public List<Guest> getAllGuests() {
        return utils.toList(guestRepo.findAll());
    }

    public List<Identification> getAllIdentifications() {
        return utils.toList(identificationRepo.findAll());
    }

    public void deleteGuest(Optional<Integer> id) {
        if (!id.isPresent()) {
            throw new NotDeletedException(0);
        }

        guestRepo.delete(guestRepo.findById(Long.valueOf(id.get())).orElseThrow(() -> new NotDeletedException(id.get())));
    }

    public void deleteIdentification(Optional<Integer> id) {
        if (!id.isPresent()) {
            throw new NotDeletedException(0);
        }

        identificationRepo.delete(identificationRepo.findById(Long.valueOf(id.get())).orElseThrow(() -> new NotDeletedException(id.get())));
    }

    public void deleteContact(long id) {
        if (!contactRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        contactRepo.deleteById(id);
    }
}