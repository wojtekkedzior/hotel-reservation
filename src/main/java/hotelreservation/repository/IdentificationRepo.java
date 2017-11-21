package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.Identification;

public interface IdentificationRepo  extends CrudRepository<Identification, Long>{

}
