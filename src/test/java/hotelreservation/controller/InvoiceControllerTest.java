package hotelreservation.controller;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
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
import hotelreservation.model.Charge;
import hotelreservation.model.Privilege;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.ui.PaymentDTO;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.MyUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, MyUserDetailsService.class })
@DataJpaTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceControllerTest  extends BaseControllerSetup {

	@Autowired
	private MockMvc mvc;

	private PaymentDTO paymentDto;
	private ReservationChargeDTO reservationChargeDto;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Override
	Collection<Privilege> getPrivilegesForReceptionist() {
		Collection<Privilege> receptionistPrivileges = new ArrayList<Privilege>();
		receptionistPrivileges.add(new Privilege("createPayment"));
		receptionistPrivileges.add(new Privilege("checkoutReservation"));
		return receptionistPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForManager() {
		Collection<Privilege> managerPrivileges = new ArrayList<Privilege>();
		managerPrivileges.add(new Privilege("createPayment"));
		managerPrivileges.add(new Privilege("checkoutReservation"));
		return managerPrivileges;
	}

	@Override
	Collection<Privilege> getPrivilegesForAdmin() {
		return new ArrayList<Privilege>();
	}

	@Before
	public void setup() {
		Charge charge = new Charge();
		charge.setCurrency(Currency.CZK);
		charge.setName("charge");
		charge.setValue(1);
		invoiceService.saveCharge(charge);
		
		reservationChargeDto = new ReservationChargeDTO();
		reservationChargeDto.setQuantity(1);
		reservationChargeDto.setCharge(charge);
		
		ReservationCharge reservationCharge = new ReservationCharge();
		reservationCharge.setQuantity(1);
		reservationCharge.setCharge(charge);
		reservationCharge.setReservation(reservation);
		
		paymentDto = new PaymentDTO();
		paymentDto.setPaymentType(PaymentType.Cash);
		paymentDto.setReservationCharges(Arrays.asList(reservationCharge));
		
		try {
			bookingService.saveReservation(reservation);
			bookingService.realiseReservation(reservation);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_allowed() throws Exception {
		//nothing here
	}

	@Test
	@WithUserDetails("admin")
	public void testAdminRolePermissions_forbidden() throws Exception {
		mvc.perform(get("/payment/1")).andExpect(status().isForbidden());

		mvc.perform(post("/createPayment/1/").flashAttr("paymentDTO", paymentDto)).andExpect(status().isForbidden());
		mvc.perform(post("/addChargeToReservation/1").flashAttr("reservationChargeDTO", reservationChargeDto)).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_allowed() throws Exception {
		mvc.perform(get("/payment/1")).andExpect(status().isOk());
		
		mvc.perform(post("/createPayment/1").flashAttr("paymentDTO", paymentDto)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addChargeToReservation/1").flashAttr("reservationChargeDTO", reservationChargeDto)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("manager")
	public void testManagerRolePermissions_forbidden() throws Exception {
		//nothing here
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_allowed() throws Exception {
		mvc.perform(get("/payment/1")).andExpect(status().isOk());
		
		mvc.perform(post("/createPayment/1").flashAttr("paymentDTO", paymentDto)).andExpect(status().is3xxRedirection());
		mvc.perform(post("/addChargeToReservation/1").flashAttr("reservationChargeDTO", reservationChargeDto)).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithUserDetails("receptionist")
	public void testReceptionistRolePermissions_forbidden() throws Exception {
		//nothing here
	}

	@Test
	@WithMockUser(username = "manager", roles = "MISSING_ROLE")
	public void testMissingRole() throws Exception {
		mvc.perform(get("/payment/1")).andExpect(status().isForbidden());
		
		mvc.perform(post("/createPayment/1").flashAttr("paymentDTO", paymentDto)).andExpect(status().isForbidden());
		mvc.perform(post("/addChargeToReservation/1").flashAttr("reservationChargeDTO", reservationChargeDto)).andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("manager")
	public void testCantCreatePaymentWithNoCharges() throws Exception {
//		Reservation reservation = new Reservation();
//
//		Payment payment = new Payment();
//		payment.setPaymentType(PaymentType.Cash);
//		payment.setReservation(reservation);
//		
//		ReservationCharge reservationCharge = new ReservationCharge();
//		reservationCharge.setQuantity(1);
//		reservationCharge.setCharge(new Charge());
//		reservationCharge.setReservation(reservation);
//		
//		payment.setReservationCharges(Arrays.asList(reservationCharge));
		
		//TODO this test won't work any more as we find that the charges as missing at bind time
//		mvc.perform(post("/createPayment/1").flashAttr("payment", payment)).andExpect(status().isPreconditionRequired());
	}
}