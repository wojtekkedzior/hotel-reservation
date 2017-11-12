package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.AmenityType;

public interface AmenityTypeRepo  extends CrudRepository<AmenityType, Long>{

}
