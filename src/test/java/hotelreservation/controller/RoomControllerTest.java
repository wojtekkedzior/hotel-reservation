package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.Application;
import hotelreservation.Utils;
import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Privilege;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import hotelreservation.model.enums.Currency;
import hotelreservation.service.MyUserDetailsService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoomControllerTest extends BaseControllerSetup {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private Utils dateConvertor;
	
	private RoomType roomTypeStandard;
	private Status operational;
	private AmenityType amenityType;
	private Room standardRoomOne;
	private Amenity amenity;

	@Override
	Collection<Privilege> getPrivilegesForReceptionist() {
		return new ArrayList<Privilege>();
	}

	@Override
	Collection<Privilege> getPrivilegesForManager() {
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		managerPrivileges.add(new Privilege("createRoomRate"));
		return managerPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForAdmin() {
		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		adminPrivileges.add(new Privilege("createAmenity"));
		adminPrivileges.add(new Privilege("createAmenityType"));
		adminPrivileges.add(new Privilege("createRoom"));
		adminPrivileges.add(new Privilege("createRoomType"));
		
		adminPrivileges.add(new Privilege("deleteRoom"));
		adminPrivileges.add(new Privilege("deleteRoomType"));
		adminPrivileges.add(new Privilege("deleteAmenity"));
		adminPrivileges.add(new Privilege("deleteRoomRate"));
		adminPrivileges.add(new Privilege("deleteAmenityType"));
		return adminPrivileges;
	}

	@Before
	public void setup() {
		User user = new User();
		user.setPassword("password");
		user.setUserName("username");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		userService.saveUser(user, superAdmin.getUserName());

		roomTypeStandard = new RoomType("Standard", "Standard room");
		roomService.saveRoomType(roomTypeStandard);
		
		operational = new Status("Operational", "Room is in operation");
		roomService.saveStatus(operational);
		
		amenityType = new AmenityType("room amenity", "comfort");
		roomService.saveAmenityType(amenityType);
		
		standardRoomOne = new Room(1, operational, roomTypeStandard, user);
		standardRoomOne.setName("Room 1");
		standardRoomOne.setDescription("The Best Room Description");
		amenity = new Amenity("pillow", "pillow", amenityType);
		amenity.setAmenityType(amenityType);
		
		List<Amenity> amenities = new ArrayList<Amenity>();
		amenities.add(amenity);
		standardRoomOne.setRoomAmenities(amenities);
		roomService.saveRoom(standardRoomOne);
		
		RoomRate roomRateTwo = new RoomRate(standardRoomOne, Currency.CZK, 1000, dateConvertor.asDate(LocalDate.of(2018, Month.JANUARY, 2)));
		roomService.saveRoomRate(roomRateTwo);
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().is4xxClientError());
		mvc.perform(get("/amenityType/1")).andExpect(status().isOk());
		mvc.perform(get("/room/1")).andExpect(status().isOk());
		mvc.perform(get("/roomType/1")).andExpect(status().isOk());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", amenityType)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addAmenity").flashAttr("amenity", amenity)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addRoom").flashAttr("room", standardRoomOne)).andExpect(status().is3xxRedirection());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isForbidden());
		RoomRate roomRate = new RoomRate();
		roomRate.setRoom(new Room());
		roomRate.setCurrency(Currency.CZK);
		roomRate.setValue(1000);
		roomRate.setDay(new Date());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", roomRate)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isOk());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate(standardRoomOne, Currency.CZK, 10, new Date()))).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", amenityType)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenity", amenity)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("room", standardRoomOne)).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		//nothing here
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", amenityType)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenity", amenity)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("room", standardRoomOne)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate(standardRoomOne, Currency.CZK, 10, new Date()))).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
//		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
//		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
	}
}