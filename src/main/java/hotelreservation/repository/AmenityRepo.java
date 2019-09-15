package hotelreservation.repository;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AmenityRepo extends CrudRepository<Amenity, Long> {
	
	public List<Amenity> findByAmenityType(AmenityType amenityType);

}
