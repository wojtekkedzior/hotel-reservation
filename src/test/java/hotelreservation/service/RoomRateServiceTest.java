package hotelreservation.service;

import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.*;
import hotelreservation.model.enums.Currency;
import hotelreservation.repository.ReservationRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoomRateServiceTest extends BaseServiceTest {

    @Autowired
    private RoomRateService roomRateService;

    @Autowired
    private RoomService roomService;

    @MockBean
    private ReservationRepo reservationRepo;

    @Autowired
    private UserService userService;

    private RoomType roomType;
    private Role managerUserType;
    private User createdBy;
    private Room room;
    private Status status;

    private AmenityType amenityTypeRoomBasic;
    private Amenity pillow;
    private Status operational;
    private RoomType roomTypeStandard;

    private Room standardRoomOne;
    private Room standardRoomTwo;

    @Before
    public void setup() {
        createAdminUser();

        roomType = new RoomType("Standard", "Standard room");
        roomService.saveRoomType(roomType);

        managerUserType = new Role("manager", "manager desc", true);
        userService.saveRole(managerUserType);

        createdBy = User.builder().userName("receptionist").password("password").firstName("firstName").lastName("lastName").role(managerUserType).createdBy(superAdmin).build();
        userService.saveUser(createdBy, superAdmin.getUserName());

        status = new Status("Status name", "Status Description");
        roomService.saveStatus(status);

        room = new Room();
        room.setRoomNumber(1);
        room.setName("The Best Room");
        room.setDescription("The Best Room Description");
        room.setStatus(status);
        room.setRoomType(roomType);
        room.setCreatedBy(createdBy);
        room.setCreatedOn(LocalDateTime.now());
        roomService.saveRoom(room);
    }

    @Test
    public void testCRUDRoomRate() {
        RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
        roomRateService.saveRoomRate(roomRate);

        LocalDate startDate = LocalDate.of(2018, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2018, Month.JANUARY, 4);

        assertEquals(1, roomRateService.getRoomRates(startDate, endDate).size());
        assertEquals(roomRate, roomRateService.getRoomRates(startDate, endDate).get(0));

        roomRate.setValue(500);
        roomRate.setDescription("Fancy");

        RoomRate updatedRoomRate = roomRateService.getRoomRateById(roomRate.getId());
        assertEquals(roomRate, updatedRoomRate);

        roomRateService.deleteRoomRate(roomRate.getId());
        assertTrue(roomRateService.getRoomRates(startDate, endDate).isEmpty());
    }

    @Test(expected = Exception.class)
    public void testAddDuplicateRoomRate() {
        RoomRate roomRate = new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2017, Month.MARCH, 15));
        roomRateService.saveRoomRate(roomRate);
        assertEquals(1, roomRateService.getAllRoomRates().size());

        roomRateService.saveRoomRate( new RoomRate(room, Currency.CZK, 1000, LocalDate.of(2017, Month.MARCH, 15)));
    }

    @Test(expected = NotFoundException.class)
    public void testGetNonExistentRoomRate() {
        roomRateService.getRoomRateById(99);
    }

    @Test(expected = NotDeletedException.class)
    public void testDeleteNonExistentRoomRate() {
        roomRateService.deleteRoomRate(99);
    }

    @Test
    public void testGetRoomRatesAsMap() {
        setupRoomRates();

        Map<Room, List<RoomRate>> roomRatesAsMap = getRoomRatesAsMap(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));

        assertTrue(roomRatesAsMap.containsKey(standardRoomOne));
        assertTrue(roomRatesAsMap.containsKey(standardRoomTwo));

        assertEquals(3, roomRatesAsMap.get(standardRoomOne).size());
        assertEquals(3, roomRatesAsMap.get(standardRoomTwo).size());
    }

    @Test
    public void testGetRoomRates() {
        setupRoomRates();

        List<RoomRate> roomRates = roomRateService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));
        assertEquals(6, roomRates.size());

        roomRates = roomRateService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 5), LocalDate.of(2018, Month.JANUARY, 6));
        assertEquals(0, roomRates.size());
    }

    @Test
    public void testGetRoomRatesLastDayExcluded() {
        setupRoomRates();

        List<RoomRate> roomRates = roomRateService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 2), LocalDate.of(2018, Month.JANUARY, 4));
        assertEquals(4, roomRates.size());
    }

    @Test
    public void testGetRoomRatesForSpecificRoom() {
        setupRoomRates();

        List<RoomRate> roomRates = roomRateService.getRoomRates(standardRoomOne, LocalDate.of(2018, Month.JANUARY, 1), LocalDate.of(2018, Month.JANUARY, 6));
        assertEquals(3, roomRates.size());

        roomRates = roomRateService.getRoomRates(LocalDate.of(2018, Month.JANUARY, 5), LocalDate.of(2018, Month.JANUARY, 6));
        assertEquals(0, roomRates.size());
    }

    @Test
    public void testGetRoomRatesPerDateSymmetrical() {
        setupRoomRates();

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 5);

        Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomRateService.getRoomRatesPerDate(start, end);

        assertEquals(3, roomRatesPerDate.size());
        assertEquals(2, roomRatesPerDate.get(start).size());
        assertEquals(2, roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3)).size());
        assertEquals(2, roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 4)).size());
        assertFalse(roomRatesPerDate.containsKey(end));
    }

    /**
     *     roomOne   roomTwo
     * 2     						x
     * 3     x				    x
     */
    @Test
    public void testGetRoomRatesPerDateFirstRoomRateEmpty() {
        saveRooms();
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
        RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate1);

        RoomRate roomRate2 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
        roomRateService.saveRoomRate(roomRate2);
        RoomRate roomRate3 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate3);

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomRateService.getRoomRatesPerDate(start, end);

        assertEquals(2, roomRatesPerDate.size());

        List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
        assertEquals(2, roomRatesForJan2.size());
        assertNull(roomRatesForJan2.get(0));
        assertEquals(roomRate2, roomRatesForJan2.get(1));

        List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
        assertEquals(2, roomRatesForJan3.size());
        assertEquals(roomRate1, roomRatesForJan3.get(0));
        assertEquals(roomRate3, roomRatesForJan3.get(1));

        assertFalse(roomRatesPerDate.containsKey(end));
    }

    /**
     *     roomOne   roomTwo
     * 2     x
     * 3     x				    x
     */
    @Test
    public void testGetRoomRatesPerDateLastRoomRateEmpty() {
        saveRooms();

        RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
        roomRateService.saveRoomRate(roomRate1);
        RoomRate roomRate2 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate2);
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2))));
        RoomRate roomRate3 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate3);

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomRateService.getRoomRatesPerDate(start, end);

        assertEquals(2, roomRatesPerDate.size());

        List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
        assertEquals(2, roomRatesForJan2.size());
        assertEquals(roomRate1, roomRatesForJan2.get(0));
        assertNull(roomRatesForJan2.get(1));

        List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
        assertEquals(2, roomRatesForJan3.size());
        assertEquals(roomRate2, roomRatesForJan3.get(0));
        assertEquals(roomRate3, roomRatesForJan3.get(1));

        assertFalse(roomRatesPerDate.containsKey(end));
    }

    /**
     *     roomOne   roomTwo  roomThree
     * 2     	 x								x
     * 3       x                x             x
     */
    @Test
    public void testGetRoomRatesPerDateMiddleRoomRateEmpty() {
        saveRooms();

        // add another room here as the 'middle' should be middle of the rooms and not dates
        Room standardRoomThree = new Room(3, operational, roomTypeStandard, createdBy);
        standardRoomThree.setName("Room 1");
        standardRoomThree.setDescription("The Best Room Description");
        standardRoomThree.setRoomAmenities(Collections.singletonList(pillow));
        roomService.saveRoom(standardRoomThree);

        RoomRate roomRate1 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
        roomRateService.saveRoomRate(roomRate1);
//		commented out on purpose to show which rate is excluded
//		roomService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
        RoomRate roomRate2 = new RoomRate(standardRoomThree, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2));
        roomRateService.saveRoomRate(roomRate2);

        RoomRate roomRate3 = new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate3);
        RoomRate roomRate4 = new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate4);
        RoomRate roomRate5 = new RoomRate(standardRoomThree, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3));
        roomRateService.saveRoomRate(roomRate5);

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomRateService.getRoomRatesPerDate(start, end);

        assertEquals(2, roomRatesPerDate.size());

        List<RoomRate> roomRatesForJan2 = roomRatesPerDate.get(start);
        assertEquals(3, roomRatesForJan2.size());
        assertEquals(roomRate1, roomRatesForJan2.get(0));
        assertNull(roomRatesForJan2.get(1));
        assertEquals(roomRate2, roomRatesForJan2.get(2));

        List<RoomRate> roomRatesForJan3 = roomRatesPerDate.get(LocalDate.of(2018, Month.JANUARY, 3));
        assertEquals(3, roomRatesForJan3.size());
        assertEquals(roomRate3, roomRatesForJan3.get(0));
        assertEquals(roomRate4, roomRatesForJan3.get(1));
        assertEquals(roomRate5, roomRatesForJan3.get(2));

        assertFalse(roomRatesPerDate.containsKey(end));
    }

    @Test
    public void testGetRoomRatesPerDateNoneAvailable() {
        saveRooms();

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Map<LocalDate, List<RoomRate>> roomRatesPerDate = roomRateService.getRoomRatesPerDate(start, end);
        assertEquals(0, roomRatesPerDate.size());
    }

    @Test(expected = MissingOrInvalidArgumentException.class)
    public void testGetRoomRatesEndBeforeStart() {
        saveRooms();

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 4);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 2);

        roomRateService.getRoomRatesPerDate(start, end);
    }

    @Test
    public void testGetAvailableRoomRatesIgnoreReservation() {
        setupRoomRates();

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Reservation reservationOne = new Reservation();
        reservationOne.setRoomRates(roomRateService.getRoomRates(standardRoomOne, start, end));

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservationOne);

        Mockito.when(reservationRepo.findInProgressAndUpComingReservations())
                .thenReturn(reservations);

        List<RoomRate> availableRoomRates = roomRateService.getAvailableRoomRates(start, end.plusDays(1), reservationOne);
        assertEquals(6, availableRoomRates.size());
    }

    @Test
    public void testGetAvailableRoomRates() {
        setupRoomRates();

        LocalDate start = LocalDate.of(2018, Month.JANUARY, 2);
        LocalDate end = LocalDate.of(2018, Month.JANUARY, 4);

        Reservation reservationOne = new Reservation();
        reservationOne.setRoomRates(roomRateService.getRoomRates(standardRoomOne, start, end));

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservationOne);

        Mockito.when(reservationRepo.findInProgressAndUpComingReservations())
                .thenReturn(reservations);

        List<RoomRate> availableRoomRates = roomRateService.getAvailableRoomRates(start, end.plusDays(1));
        assertEquals(3, availableRoomRates.size());
    }

    private Map<Room, List<RoomRate>> getRoomRatesAsMap(LocalDate startDate, LocalDate endDate) {
		return roomRateService.getAvailableRoomRates(startDate, endDate).stream()
                .collect(Collectors.groupingBy(RoomRate::getRoom, HashMap::new, Collectors.toList()));
    }

    private void saveRooms() {
        managerUserType = new Role("manager", "manager desc", true);
        userService.saveRole(managerUserType);

        amenityTypeRoomBasic = new AmenityType("Basic", "Basic Room amenity Type");
        roomService.saveAmenityType(amenityTypeRoomBasic);

        pillow = new Amenity("pillow", "pillow", amenityTypeRoomBasic);
        roomService.saveAmenity(pillow);

        roomTypeStandard = new RoomType("Standard", "Standard room");
        roomService.saveRoomType(roomTypeStandard);

        operational = new Status("Operational", "Room is in operation");
        roomService.saveStatus(operational);

        standardRoomOne = new Room(1, operational, roomTypeStandard, createdBy);
        standardRoomOne.setName("Room 1");
        standardRoomOne.setDescription("The Best Room Description");
        standardRoomOne.setRoomAmenities(Collections.singletonList(pillow));
        roomService.saveRoom(standardRoomOne);

        standardRoomTwo = new Room(2, operational, roomTypeStandard, createdBy);
        standardRoomTwo.setName("Room 2");
        standardRoomTwo.setDescription("The Best Room Description");
        standardRoomTwo.setRoomAmenities(Collections.singletonList(pillow));
        roomService.saveRoom(standardRoomTwo);
    }

    private void setupRoomRates() {
        saveRooms();

        roomRateService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
        roomRateService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3)));
        roomRateService.saveRoomRate(new RoomRate(standardRoomOne, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4)));

        roomRateService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 2)));
        roomRateService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 3)));
        roomRateService.saveRoomRate(new RoomRate(standardRoomTwo, Currency.CZK, 1000, LocalDate.of(2018, Month.JANUARY, 4)));
    }
}