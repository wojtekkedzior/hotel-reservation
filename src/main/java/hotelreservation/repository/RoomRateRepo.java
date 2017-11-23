package hotelreservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
	List<RoomRate> findByStartDateBetween(LocalDate start, LocalDate end);
	
	List<RoomRate> findByStartDateAfterAndEndDateBefore(LocalDate start, LocalDate end);
	
	List<RoomRate> findByStartDateAfter (LocalDate start);
}