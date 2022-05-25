package hotelreservation.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.repository.ReservationRepo;
import hotelreservation.repository.RoomRateRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomRateService {
    private final RoomRateRepo roomRateRepo;
    private final ReservationRepo reservationRepo;
    private final Utils utils;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public RoomRate saveRoomRate(RoomRate roomRate) {
        return roomRateRepo.save(roomRate);
    }

    public RoomRate getRoomRateById(long id) {
        log.info("Looking for RoomRate with ID: {}", id);
        return roomRateRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<RoomRate> getAllRoomRates() {
        return utils.toList(roomRateRepo.findAll());
    }

    public void deleteRoomRate(long id) {
        if (!roomRateRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        roomRateRepo.deleteById(id);
    }

    /**
     *
     * @param start -   included
     * @param end - excluded
     * @return Available room rates between the start date and end date (end -1)
     */
    public List<RoomRate> getRoomRates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new MissingOrInvalidArgumentException("End date:" + end + " cannot be before start date: " + start);
        }

        List<RoomRate> findByDayBetween = start.isEqual(end) ? roomRateRepo.findByDay(start) : roomRateRepo.findByDayBetween(start, end.minus(Period.ofDays(1)));
        log.info("Looking for all RoomRates between: {} and: {} -  Found: {}", start, end, findByDayBetween.size());
        return findByDayBetween;
    }

    public List<RoomRate> getRoomRates(Room room, LocalDate start, LocalDate end) {
        List<RoomRate> findByStartDateBetween = roomRateRepo.findByRoomIdAndDayBetween(room.getId(), start, end);
        log.info("Looking for all RoomRates between: {} and: {} for Room: {} - Found: {}", start, end, room.getId(), findByStartDateBetween.size());
        return findByStartDateBetween;
    }

    public List<RoomRate> getAvailableRoomRates(LocalDate start, LocalDate end) {
        return filterAvailableRoomRates(start, end, reservationRepo.findInProgressAndUpComingReservations());
    }

    public List<RoomRate> getAvailableRoomRates(LocalDate start, LocalDate end, Reservation reservationToIgnore) {
        List<Reservation> inProgressAndUpComingReservations = reservationRepo.findInProgressAndUpComingReservations();
        inProgressAndUpComingReservations.remove(reservationToIgnore);
        return filterAvailableRoomRates(start, end, inProgressAndUpComingReservations);
    }

    private List<RoomRate> filterAvailableRoomRates(LocalDate start, LocalDate end, List<Reservation> reservations) {
        Set<RoomRate> takenRoomRates = reservations.stream()
                .map(Reservation::getRoomRates)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        return getRoomRates(start, end).stream()
                .filter(r -> !takenRoomRates.contains(r))
                .collect(Collectors.toList());
    }

    public long getRoomRateCount() {
        return roomRateRepo.count();
    }

    /**
     * Returns a Map of available room rates as a list for each day in the range provided. Any missing days are replaced with null so that the .size() of each list is the same.
     * Available rates will also always be in the same index across all lists.
     * <p>
     * The returned map is sorted by the room id (this will be changed later to room number) so that the UI can display the rooms ascending.
     *
     * @param start
     * @param end
     * @return
     */
    public Map<LocalDate, List<RoomRate>> getRoomRatesPerDate(LocalDate start, LocalDate end) {
        Map<LocalDate, List<RoomRate>> roomRatesAsMapByDates = new TreeMap<>();
        List<RoomRate> availableRoomRates = getAvailableRoomRates(start, end); //this method is wrong. for the 13th to the 15th it should only return rates for the 13th and 14th

        Map<Room, List<RoomRate>> roomRatesPerRoom = availableRoomRates.stream()
                .collect(Collectors.groupingBy(RoomRate::getRoom, TreeMap::new, Collectors.toList()));

        start.datesUntil(end)
                .forEach(day -> roomRatesPerRoom
                        .forEach((room, roomRates) -> roomRates.stream()
                                .filter(roomRate -> roomRate.getDay().isEqual(day))
                                .findFirst()
                                .ifPresentOrElse(roomRate -> roomRatesAsMapByDates.computeIfAbsent(day, k -> new LinkedList<>()).add(roomRate),
                                        () -> roomRatesAsMapByDates.computeIfAbsent(day, k -> new LinkedList<>()).add(null))));

        return roomRatesAsMapByDates;
    }
}




