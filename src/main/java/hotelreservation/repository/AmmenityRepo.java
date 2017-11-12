package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Amenity;

public interface AmmenityRepo extends CrudRepository<Amenity, Long> {

}
