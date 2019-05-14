package hotelreservation.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
	List<RoomRate> findByDayBetween(LocalDate start, LocalDate end);
	
	List<RoomRate> findByDayAfter (Date start);
	
	RoomRate findByDay(Date day);
	
	List<RoomRate> findByRoomId(long roomId);

	List<RoomRate> findByRoomIdAndDayBetween(long id, LocalDate startDate, LocalDate endDate);
} 