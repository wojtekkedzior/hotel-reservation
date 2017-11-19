package hotelreservation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;

public interface AmenityRepo extends CrudRepository<Amenity, Long> {
	
	public List<Amenity> findByAmenityType(AmenityType amenityType);

}
