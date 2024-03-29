package hotelreservation.controller;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.finance.Payment;
import hotelreservation.model.ui.PaymentDTO;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
public class InvoiceController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final InvoiceService invoiceService;
	private final BookingService bookingService;

	@GetMapping(value = { "/invoice/{id}" })
	@PreAuthorize("hasAuthority('retrieveInvoice')")
	public String getInvoice(@PathVariable Long id, Model model) {
		return "invoice";
	}
	
	@GetMapping(value = { "/payment/{id}" })
	@PreAuthorize("hasAuthority('createPayment')")
	public String getPayment(@PathVariable Long id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		
		model.addAttribute("reservation", reservation);
		model.addAttribute("outstandingCharges", invoiceService.getOutstandingCharges(reservation));
		model.addAttribute("payment", new PaymentDTO());
		model.addAttribute("formsOfPayment", PaymentType.values());
		model.addAttribute("reservationCharge",  invoiceService.getAllReservationChargesForAReservation(reservation));
		
		return "payment";
	}
	
	//To make @RequestParam(value = "charges" , required = false) int[] charges , just add name="charges to an input field of type checkbox"
	
	@PostMapping("/createPayment/{reservationId}")
	@PreAuthorize("hasAuthority('createPayment')")
	public ModelAndView createPayment(@Valid @ModelAttribute PaymentDTO paymentDto, @PathVariable Long reservationId) {
		log.info("creating payment for reservation: {}", reservationId);

		Payment payment = new Payment();
		payment.setPaymentDate(LocalDateTime.now()) ;
		payment.setReservation(bookingService.getReservation(reservationId));
		payment.setPaymentType(paymentDto.getPaymentType());
		payment.setReservationCharges(paymentDto.getReservationCharges());

		invoiceService.savePayment(payment);

		return new ModelAndView("redirect:/checkoutReservation/" + reservationId);
	}
	
	@PostMapping("/addChargeToReservation/{reservationId}")
	@PreAuthorize("hasAuthority('checkoutReservation')")
	public ModelAndView addChargeToReservation(@Valid @ModelAttribute ReservationChargeDTO reservationChargeDto, @PathVariable Long reservationId) {
		log.info("adding charge to reservation: {}", reservationId);

		ReservationCharge reservationCharge = new ReservationCharge();
		Reservation reservation = bookingService.getReservation(reservationId);
		reservationCharge.setReservation(reservation);
		reservationCharge.setQuantity(reservationChargeDto.getQuantity());
		reservationCharge.setCharge(reservationChargeDto.getCharge());
		
		invoiceService.saveReservationCharge(reservationCharge);

		return new ModelAndView("redirect:/checkoutReservation/" + reservationId);
	}
}