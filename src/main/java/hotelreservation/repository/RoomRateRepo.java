package hotelreservation.repository;

import hotelreservation.model.RoomRate;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
	List<RoomRate> findByDayBetween(LocalDate start, LocalDate end);
	
	List<RoomRate> findByDayAfter (Date start);
	
	RoomRate findByDay(Date day);
	
	List<RoomRate> findByRoomId(long roomId);

	List<RoomRate> findByRoomIdAndDayBetween(long id, LocalDate startDate, LocalDate endDate);
} 