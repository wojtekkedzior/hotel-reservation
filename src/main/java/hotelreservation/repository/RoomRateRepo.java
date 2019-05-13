package hotelreservation.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
//    default void delByFecha(LocalDate fecha) {
//        deleteByFechaBetween(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
//    }
	
//	default void findByDayBetween(LocalDate start, LocalDate end) {
//		findByDayBetween(start.atStartOfDay(), end.atStartOfDay());
//	}
	
	List<RoomRate> findByDayBetween(LocalDate start, LocalDate end);
	
//	@Query("SELECT COUNT(entity) FROM Person entity WHERE trunc(entity.date) BETWEEN :startTime AND :endTime")
//	int getPersonBetweenDates(@Param("startTime") Date var1, @Param("endTime") Date var2);
	
//	List<RoomRate> findByDayAfterAndEndDateBefore(LocalDate start, LocalDate end);
	
	List<RoomRate> findByDayAfter (Date start);
	
	RoomRate findByDay(Date day);
	
	List<RoomRate> findByRoomId(long roomId);

	List<RoomRate> findByRoomIdAndDayBetween(long id, Date startDate, Date endDate);
} 