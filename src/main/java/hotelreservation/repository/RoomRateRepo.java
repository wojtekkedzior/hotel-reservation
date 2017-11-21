package hotelreservation.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hotelreservation.model.RoomRate;

public interface RoomRateRepo extends CrudRepository<RoomRate, Long> {
	
	List<RoomRate> findByStartDateBetween(Date start, Date end);
	
//	@Query("select b from Book b " +
//	         "where b.from between ?1 and ?2 and b.to between ?1 and ?2")
//	  List<Book> findByDatesBetween(Date departure, Date arrival);

}
