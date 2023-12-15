package hotelreservation.repository;

import hotelreservation.model.Reservation;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepo extends CrudRepository<Reservation, Long> {
	
	List<Reservation> findByStartDate(LocalDate startDate);
	
	List<Reservation> findByStartDateBetween(Date start, Date end);
	
	@Query(value="select * from \"reservation\" where \"reservation_status\" = 'UP_COMING' or \"reservation_status\" = 'IN_PROGRESS' or \"reservation_status\" = 'ABANDONED' ", nativeQuery=true)
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
