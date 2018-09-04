package hotelreservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;

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
import hotelreservation.model.Privilege;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.service.BookingService;
import hotelreservation.service.MyUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationControllerTest extends BaseControllerSetup {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private BookingService bookingService;

	@Override
	Collection<Privilege> getPrivilegesForReceptionist() {
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();
		receptionistPrivileges.add(new Privilege("getReservation"));
		receptionistPrivileges.add(new Privilege("createReservation"));
		receptionistPrivileges.add(new Privilege("cancelReservation"));
		receptionistPrivileges.add(new Privilege("realiseReservation"));
		receptionistPrivileges.add(new Privilege("checkoutReservation"));
		receptionistPrivileges.add(new Privilege("fulfillReservation"));
		receptionistPrivileges.add(new Privilege("viewReservationDashBoard"));
		return receptionistPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForManager() {
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		managerPrivileges.add(new Privilege("getReservation"));
		managerPrivileges.add(new Privilege("createReservation"));
		managerPrivileges.add(new Privilege("cancelReservation"));
		managerPrivileges.add(new Privilege("realiseReservation"));
		managerPrivileges.add(new Privilege("checkoutReservation"));
		managerPrivileges.add(new Privilege("fulfillReservation"));
		managerPrivileges.add(new Privilege("viewReservationDashBoard"));
		return managerPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForAdmin() {
		Collection<Privilege> adminPrivileges = new ArrayList<Privilege>();
		adminPrivileges.add(new Privilege("deleteReservation"));
		adminPrivileges.add(new Privilege("viewReservationDashBoard"));
		return adminPrivileges;
	}

	@Before
	public void setup() {
		try {
			bookingService.saveReservation(reservation);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().is3xxRedirection());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", reservation)).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addOccupant/1").flashAttr("guest", guest)).andExpect(status().isForbidden());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().isForbidden());

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(reservation);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1").flashAttr("reservation", reservation)).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		mvc.perform(post("/addOccupant/1").flashAttr("guest", guest)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1")).andExpect(status().is4xxClientError());

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(reservation);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isOk());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isOk());
		mvc.perform(post("/realiseReservation/1")).andExpect(status().is3xxRedirection());
		//TODO add addOccupant to these tests
		//		mvc.perform(post("/addOccupant/1")).andExpect(status().is3xxRedirection());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isOk());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isOk());
		
		
		mvc.perform(get("/dashboard")).andExpect(status().isOk());
		mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		mvc.perform(post("/addOccupant/1").flashAttr("guest", guest)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/fulfillReservation/1").flashAttr("reservationID", 1)).andExpect(status().is4xxClientError());

		ReservationCancellation cancellation = new ReservationCancellation();
		cancellation.setReason("some reason");
		cancellation.setReservation(reservation);
		mvc.perform(post("/cancelReservation/1").flashAttr("reservationCancellation", cancellation)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		mvc.perform(delete("/reservationDelete/{id}", new Integer(1))).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/reservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/realiseReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/cancelReservation/1")).andExpect(status().isForbidden());
		mvc.perform(get("/checkoutReservation/1")).andExpect(status().isForbidden());
		mvc.perform(delete("/reservationDelete/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/addOccupant/1").flashAttr("guest", guest)).andExpect(status().isForbidden());
	}
}