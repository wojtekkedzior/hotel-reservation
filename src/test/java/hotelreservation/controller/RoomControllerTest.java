package hotelreservation.controller;

import hotelreservation.ApplicationStartup;
import hotelreservation.RestExceptionHandler;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.ui.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
	
	private RoomTypeDTO roomTypeStandardDTO = new RoomTypeDTO("roomType", "roomType");

	private AmenityTypeDTO amenityTypeDTO = new AmenityTypeDTO("amenity", "desc");
	private AmenityDTO amenityDTO;

	private RoomRateDTO roomRateDTO;
	private RoomDTO roomDTO;

	@Before
	public void setup() {
		this.mvc = standaloneSetup(roomController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
		amenityDTO = new AmenityDTO("name", "description", applicationStartup.amenityTypeRoomBasic);
		roomRateDTO = new RoomRateDTO("description", applicationStartup.standardRoomOne, Currency.CZK, 10, LocalDate.now());
		roomDTO = new RoomDTO(1, applicationStartup.operational, "name", "description", null, applicationStartup.roomTypeStandard, LocalDateTime.now(), applicationStartup.admin);
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
		mvc.perform(post("/addRoomType").flashAttr("roomTypeDTO", roomTypeStandardDTO)).andExpect(status().is3xxRedirection());

		mvc.perform(post("/addRoom").flashAttr("roomDTO", roomDTO)).andExpect(status().is3xxRedirection());
		
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
		mvc.perform(post("/addRoomRate").flashAttr("roomRateDTO", roomRateDTO)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/roomRate/1")).andExpect(status().isOk());
		mvc.perform(post("/addRoomRate").flashAttr("roomRateDTO", new RoomRateDTO("description", applicationStartup.standardRoomOne, Currency.CZK, 10, LocalDate.now().plus(1, ChronoUnit.YEARS)))).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/amenity/1")).andExpect(status().isForbidden());
		mvc.perform(get("/amenityType/1")).andExpect(status().isForbidden());
		mvc.perform(get("/room/1")).andExpect(status().isForbidden());
		mvc.perform(get("/roomType/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityTypeDTO", amenityTypeDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenityDTO", amenityDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomTypeDTO", roomTypeStandardDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("roomDTO", roomDTO)).andExpect(status().isForbidden());
		
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
		
		mvc.perform(post("/addAmenityType").flashAttr("amenityTypeDTO", amenityTypeDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addAmenity").flashAttr("amenityDTO", amenityDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoomType").flashAttr("roomTypeDTO", roomTypeStandardDTO)).andExpect(status().isForbidden());
		mvc.perform(post("/addRoom").flashAttr("roomDTO", roomDTO)).andExpect(status().isForbidden());

		mvc.perform(post("/addRoomRate").flashAttr("roomRateDTO", roomRateDTO)).andExpect(status().isForbidden());
		
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
	public void testGetAmenityWithMissingId() throws Exception {
		mvc.perform(get("/amenity/99999")).andExpect(status().is4xxClientError());
	}

	//----------------------------------------------------------------------------
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
	public void testGetAmenityTypeWithMissingId() throws Exception {
		mvc.perform(get("/amenityType/99999")).andExpect(status().is4xxClientError());
	}

	//----------------------------------------------------------------------------
	@Test
	@WithUserDetails("admin")
	public void testGetRoomWithNoId() throws Exception {
		mvc.perform(get("/room/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetRoomWithMissingId() throws Exception {
		mvc.perform(get("/room/9999")).andExpect(status().is4xxClientError());
	}

	//------------------------------------------------------------------------------
	@Test
	@WithUserDetails("admin")
	public void testGetRoomTypeWithNoId() throws Exception {
		mvc.perform(get("/roomType/")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin")
	public void testGetRoomTypeWithMissingId() throws Exception {
		mvc.perform(get("/roomType/9999")).andExpect(status().is4xxClientError());
	}

	//----------------------------------------------------------------------------

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
	public void testGetRoomRateWithMissingId() throws Exception {
		mvc.perform(get("/roomRate/9999")).andExpect(status().is4xxClientError());
	}

	//---Delete
	//----------------------------------------------------------------------------
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

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomTypeWithNoId() throws Exception {
		mvc.perform(delete("/roomType/")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomRateWithNoId() throws Exception {
		mvc.perform(delete("/roomDelete/")).andExpect(status().is4xxClientError());
	}

	//----------------------------------------------------------------------------
	@Test
	@WithUserDetails("manager")
	public void testDeleteAmenityWithMissingId() throws Exception {
		mvc.perform(delete("/amenityDelete/9999")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("manager")
	public void testDeleteAmenityTypeWithMissingId() throws Exception {
		mvc.perform(delete("/amenityTypeDelete/")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomWithMissingId() throws Exception {
		mvc.perform(delete("/roomRate/9999")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomTypeMissingId() throws Exception {
		mvc.perform(delete("/roomType/9999")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomRateMissingNoId() throws Exception {
		mvc.perform(delete("/roomDelete/9999")).andExpect(status().is2xxSuccessful());
	}

	//----------------------------------------------------------------------------
	@Test
	@WithUserDetails("admin")
	public void testDeleteAmenity() throws Exception {
		mvc.perform(delete("/amenityDelete/1")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testDeleteAmenityType() throws Exception {
		mvc.perform(delete("/amenityTypeDelete/1")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoom() throws Exception {
		mvc.perform(delete("/roomRate/1")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomTypeW() throws Exception {
		mvc.perform(delete("/roomType/1")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithUserDetails("admin")
	public void testDeleteRoomRate() throws Exception {
		mvc.perform(delete("/roomDelete/1")).andExpect(status().is4xxClientError());
	}

}