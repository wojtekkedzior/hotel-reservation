package hotelreservation.service;

import hotelreservation.Utils;
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.*;
import hotelreservation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomService {
    private final RoomRepo roomRepo;
    private final RoomTypeRepo roomTypeRepo;
    private final StatusRepo statusRepo;
    private final AmenityTypeRepo amenityTypeRepo;
    private final AmenityRepo amenityRepo;
    private final Utils utils;

    public List<Amenity> getRoomAmenities() {
        return utils.toList(amenityTypeRepo.findAll()).stream()
                .filter(t -> t.getName().equals("Hotel"))
                .map(amenityRepo::findByAmenityType)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<Room> getByRoomsByStatus(Status status) {
        return utils.toList(roomRepo.findByStatus(status));
    }

//    public List<RoomRate> getRoomRates(LocalDate start, LocalDate end) {
//        if (end.isBefore(start)) {
//            throw new MissingOrInvalidArgumentException("End date:" + end + " cannot be before start date: " + start);
//        }
//
//        List<RoomRate> findByDayBetween = start.isEqual(end) ? roomRateRepo.findByDay(start) : roomRateRepo.findByDayBetween(start, end.minus(Period.ofDays(1)));
//        log.info("Looking for all RoomRates between: {} and: {} -  Found: {}", start, end, findByDayBetween.size());
//        return findByDayBetween;
//    }


    //---- Amenity
    public Amenity saveAmenity(Amenity amenity) {
        return amenityRepo.save(amenity);
    }

    public Amenity getAmenityById(long id) {
        log.info("Looking for Amenity with ID: {}", id);
        return amenityRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<Amenity> getAllAmenities() {
        return utils.toList(amenityRepo.findAll());
    }

    public void deleteAmenity(long id) {
        if (!amenityRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        amenityRepo.deleteById(id);
    }

    //---- AmenityType
    public AmenityType saveAmenityType(AmenityType amenityType) {
        return amenityTypeRepo.save(amenityType);
    }

    public AmenityType getAmenityTypeById(long id) {
        log.info("Looking for AmenityType with ID: {}", id);
        return amenityTypeRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<AmenityType> getAllAmenityTypes() {
        return utils.toList(amenityTypeRepo.findAll());
    }

    public void deleteAmenityType(long id) {
        if (!amenityTypeRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        amenityTypeRepo.deleteById(id);
    }

    //---- Room
    public Room saveRoom(Room room) {
        room.setCreatedOn(LocalDateTime.now());
        return roomRepo.save(room);
    }

    public Room getRoomById(long id) {
        log.info("Looking for Room with ID: {}", id);
        return roomRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<Room> getAllRooms() {
        return utils.toList(roomRepo.findAll());
    }

    public void deleteRoomById(long id) {
        if (!roomRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        roomRepo.deleteById(id);
    }

    public void deleteRoom(Room room) {
        if (!roomRepo.existsById(room.getId())) {
            throw new NotDeletedException(room.getId());
        }
        roomRepo.delete(room);
    }

    //---- RoomType
    public RoomType saveRoomType(RoomType roomType) {
        return roomTypeRepo.save(roomType);
    }

    public RoomType getRoomTypeById(long id) {
        log.info("Looking for RoomType with ID: {}", id);
        return roomTypeRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<RoomType> getAllRoomTypes() {
        return utils.toList(roomTypeRepo.findAll());
    }

    public void deleteRoomType(Long id) {
        if (!roomTypeRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        roomTypeRepo.deleteById(id);
    }

    //---- Status
    public Status saveStatus(Status status) {
        return statusRepo.save(status);
    }

    public Status getStatusById(long id) {
        log.info("Looking for Status with ID: {} ", id);
        return statusRepo.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<Status> getAllStatuses() {
        return utils.toList(statusRepo.findAll());
    }

    public void deleteStatus(long id) {
        if (!statusRepo.existsById(id)) {
            throw new NotDeletedException(id);
        }
        statusRepo.deleteById(id);
    }

    public long getRoomsCount() {
        return roomRepo.count();
    }


}