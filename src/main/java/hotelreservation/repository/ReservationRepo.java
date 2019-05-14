package hotelreservation.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hotelreservation.model.Reservation;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.ReservationStatus;

@Repository
public interface ReservationRepo extends CrudRepository<Reservation, Long> {
	
	List<Reservation> findByStartDate(LocalDate startDate);
	
	List<Reservation> findByStartDateBetween(Date start, Date end);
	
	@Query(value="select * from reservation where reservation_status = 'UpComing' or reservation_status = 'InProgress'", nativeQuery=true)
	List<Reservation> findInProgressAndUpComingReservations();
	
//	@Query("SELECT e FROM MyEntity e WHERE e.validFrom < CURRENT_DATE")
//	List<RoomRate> findBystartDateGreaterThanAndendDateLessThan(Date start, Date end);
	
	List<RoomRate> findBystartDateGreaterThan(Date start);

	List<Reservation> findByReservationStatus(ReservationStatus status);

	
	
	
	

//    @Query("select c from Country c where c.continent.name = ?1 and c.population < ?2")
//    public List<Country> findByContinentNameAndPopulationLessThanQuery(String continentName, int pop)

	
//    @Query("select c from Country c where c.name != :name")
//    public List<Country> findByNameNotQuery(@Param("name") String countryName);
    
    
}
