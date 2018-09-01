package hotelreservation.controller;

import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.finance.Payment;
import hotelreservation.model.ui.PaymentDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;


@Controller
public class InvoiceController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private BookingService bookingService;
	
	@RequestMapping(value = { "/invoice/{id}" }, method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('retrieveInvoice')")
	public String getInvoice(@PathVariable Optional<Integer> id, Model model) {
		return "invoice";
	}
	
	@RequestMapping(value = { "/payment/{id}" }, method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('createPayment')")
	public String getPayment(@PathVariable Optional<Integer> id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		
		model.addAttribute("reservation", reservation);
		model.addAttribute("outstandingCharges", invoiceService.getOutstandingCharges(reservation));
		model.addAttribute("payment", new Payment());
		model.addAttribute("formsOfPayment", PaymentType.values());
		model.addAttribute("reservationCharge",  invoiceService.getAllReservationChargesForAReservation(reservation));
		
		return "payment";
	}
	
	//To make @RequestParam(value = "charges" , required = false) int[] charges , just add name="charges to an input field of type checkbox"
	
	@PostMapping("/createPayment/{reservationId}")
	@PreAuthorize("hasAuthority('createPayment')")
	public ModelAndView createPayment(@Valid @ModelAttribute PaymentDTO paymentDto,  @PathVariable Optional<Integer> reservationId) {
		log.info("creating payment for reservation: " + reservationId);
		
		Payment payment = new Payment();
		payment.setPaymentDate(new Date());
		
		//TODO is the reservationID param usefull here?
		Reservation reservation = bookingService.getReservation(reservationId);
		payment.setReservation(reservation);
		
		payment.setPaymentType(paymentDto.getPaymentType());
		payment.setReservationCharges(paymentDto.getReservationCharges());

		invoiceService.savePayment(payment);
		
		//TODO use credit card in reservation?
		// gather all payment details and call createpayment.  if successful generate invoice and show 'show invoice' button to download/display the invoice
		//payment needs to have any extra costs 
		
		return new ModelAndView("redirect:/checkoutReservation/" + reservationId.get());
	}
	
	//TODO reservationCharge should be a slightly different object, as in without the  reservation
	@PostMapping("/addChargeToReservation/{reservationId}")
	@PreAuthorize("hasAuthority('checkoutReservation')")
	public ModelAndView addChargeToReservation(@Valid @ModelAttribute ReservationCharge reservationCharge, @PathVariable Optional<Integer> reservationId) {
		Reservation reservation = bookingService.getReservation(reservationId);
		reservationCharge.setReservation(reservation);
		
		invoiceService.saveReservationCharge(reservationCharge);
		return new ModelAndView("redirect:/checkoutReservation/" + reservationId.get());
	}
}