package hotelreservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;

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
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.ui.PaymentDTO;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceControllerTest  {

	private PaymentDTO paymentDto;
	private ReservationChargeDTO reservationChargeDto;
	
	private MockMvc mvc;
	
	@Autowired
	private ApplicationStartup applicationStartup;
	
	@Autowired
	private InvoiceController invoiceController;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private BookingService bookingService;
	
	@Before
	public void setup() {
		this.mvc = standaloneSetup(invoiceController) .setControllerAdvice(new RestExceptionHandler()).build();// Standalone context
		
		bookingService.realiseReservation(applicationStartup.reservationOne);
		
		reservationChargeDto = new ReservationChargeDTO();
		reservationChargeDto.setQuantity(1);
		reservationChargeDto.setCharge(applicationStartup.coke);
		
		ReservationCharge reservationCharge = new ReservationCharge();
		reservationCharge.setQuantity(1);
		reservationCharge.setCharge(applicationStartup.roomServiceDelivery);
		reservationCharge.setReservation(applicationStartup.reservationOne);
		invoiceService.saveReservationCharge(reservationCharge);
		
		paymentDto = new PaymentDTO();
		paymentDto.setPaymentType(PaymentType.Cash);
		paymentDto.setReservationCharges(Arrays.asList(reservationCharge));
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