package hotelreservation.repository;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo  extends CrudRepository<RoomRate, Long>{

}
