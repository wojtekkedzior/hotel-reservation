package hotelreservation.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.exceptions.PaymentNotCreatedException;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import hotelreservation.model.finance.Payment;
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
	
	@PostMapping("/createPayment/{id}")
	@PreAuthorize("hasAuthority('createPayment')")
	public ModelAndView createPayment(@ModelAttribute Payment payment,  @PathVariable Optional<Integer> id, BindingResult bindingResult) {
		log.info("creating paymeny for reservation: " + id);
		
		if(payment.getReservationCharges() == null || payment.getReservationCharges().isEmpty()) {
			throw new PaymentNotCreatedException(id.get());
		}
		
		Reservation reservation = bookingService.getReservation(id);

		//TODO still need to understand why the id field of payment is getting set to the value of the id field which is meant for the reservation.
		//obviously it's in the naming convention
		payment.setId(0);
		payment.setReservation(reservation);
		invoiceService.savePayment(payment);
		
		//TODO use credit card in reservation?
		// gather all payment details and call createpayment.  if successful generate invoice and show 'show invoice' button to download/display the invoice
		//payment needs to have any extra costs 
		
		return new ModelAndView("redirect:/checkoutReservation/" + id.get());
	}
	
	@PostMapping("/addChargeToReservation/{reservationID}")
	@PreAuthorize("hasAuthority('checkoutReservation')")
	public ModelAndView addChargeToReservation(@ModelAttribute ReservationCharge reservationCharge, @PathVariable Optional<Integer> reservationID) {
		Reservation reservation = bookingService.getReservation(reservationID);
		reservationCharge.setId(0); // TODO need to figure out why the ID is being set. in this case the reservation ID is also placed into the ReservationCancellation
		reservationCharge.setReservation(reservation);
		
		invoiceService.saveReservationCharge(reservationCharge);
		return new ModelAndView("redirect:/checkoutReservation/" + reservationID.get());
	}
}