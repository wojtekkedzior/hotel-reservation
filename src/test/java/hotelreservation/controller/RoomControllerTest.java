package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import hotelreservation.model.ui.AmenityDTO;
import hotelreservation.model.ui.AmenityTypeDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.enums.Currency;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoomControllerTest {

	private MockMvc mvc;
	
	@Autowired
	private ApplicationStartup applicationStartup;

	@Autowired
	private RoomController roomController;
	
	private RoomType roomTypeStandard = new RoomType("roomType", "roomType");

	private AmenityTypeDTO amenityTypeDTO = new AmenityTypeDTO("amenity", "desc");
	private AmenityDTO amenityDTO;

	@Before
	public void setup() {
		this.mvc = standaloneSetup(roomController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
		amenityDTO = new AmenityDTO("name", "description", applicationStartup.amenityTypeRoomBasic);
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isOk());
		mvc.perform(get("/amenityType/1")).andExpect(status().isOk());
		mvc.perform(get("/room/1")).andExpect(status().isOk());
		mvc.perform(get("/roomType/1")).andExpect(status().isOk());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityTypeDTO", amenityTypeDTO)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addAmenity").flashAttr("amenityDTO", amenityDTO)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().is3xxRedirection());
		
		Room room = new Room(1, applicationStartup.operational, applicationStartup.roomTypeStandard, applicationStartup.admin);
		room.setCreatedOn(LocalDateTime.now());
		mvc.perform(post("/addRoom").flashAttr("room", room)).andExpect(status().is3xxRedirection());
		
		//Some of these deletes will fail because of constraint violations, which is fine.
		mvc.perform(delete("/roomDelete/1")).andExpect(status().is4xxClientError());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().is4xxClientError());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().is3xxRedirection());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().is4xxClientError());
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
		roomRate.setDay(LocalDate.now());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", roomRate)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isOk());
		
		RoomRate roomRate = new RoomRate(applicationStartup.standardRoomOne, Currency.CZK, 10, LocalDate.now().plus(1, ChronoUnit.YEARS));
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", roomRate)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", applicationStartup.amenityTypeRoomBasic)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenity", amenityDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("room", applicationStartup.standardRoomOne)).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityType", applicationStartup.amenityTypeRoomBasic)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenity", amenityDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomType", roomTypeStandard)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("room", applicationStartup.standardRoomOne)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomRate").flashAttr("roomRate", new RoomRate(applicationStartup.standardRoomOne, Currency.CZK, 10, LocalDate.now()))).andExpect(status().isForbidden());
		
		mvc.perform(delete("/roomDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/roomRateDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomRate/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetAmenityWithNoId() throws Exception {
		mvc.perform(get("/amenity/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetAmenity() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetAmenityTypeWithNoId() throws Exception {
		mvc.perform(get("/amenityType/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetAmenityType() throws Exception {
		mvc.perform(get("/amenityType/1")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetRoomWithNoId() throws Exception {
		mvc.perform(get("/room/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetRoom() throws Exception {
		mvc.perform(get("/room/1")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("manager")
	public void testGetRoomWRateWithNoId() throws Exception {
		mvc.perform(get("/roomRate/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("manager")
	public void testGetRoomRate() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("manager")
	public void testDeleteAmenityWithNoId() throws Exception {
		mvc.perform(delete("/amenityDelete/")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("manager")
	public void testDeleteAmenityTypeWithNoId() throws Exception {
		mvc.perform(delete("/amenityTypeDelete/")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomWithNoId() throws Exception {
		mvc.perform(delete("/roomRate/")).andExpect(status().is4xxClientError());
	}

}