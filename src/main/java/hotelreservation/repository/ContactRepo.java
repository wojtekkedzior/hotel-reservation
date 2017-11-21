package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Contact;

@Repository
public interface ContactRepo extends CrudRepository<Contact, Long> {

}
