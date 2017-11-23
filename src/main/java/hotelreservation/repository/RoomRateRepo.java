package hotelreservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
	List<RoomRate> findByDayBetween(LocalDate start, LocalDate end);
	
//	List<RoomRate> findByDayAfterAndEndDateBefore(LocalDate start, LocalDate end);
	
	List<RoomRate> findByDayAfter (LocalDate start);
}