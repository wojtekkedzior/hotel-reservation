package hotelreservation.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import hotelreservation.model.Guest;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCheckout;
import hotelreservation.model.finance.Payment;

@Controller
public class InvoiceController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = { "/invoice/{id}" }, method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('retrieveInvoice')")
	public String getInvoice(@PathVariable Optional<Integer> id, Model model) {

		return "invoice";
	}
	
	@PostMapping("/createPayment")
	@PreAuthorize("hasAuthority('createPayment')")
	public ModelAndView createPayment(@ModelAttribute Payment payment, BindingResult bindingResult) {

		//TODO use credit card in reservation?
		// gather all payment details and call createpayment.  if successful generate invoice and show 'show invoice' button to download/display the invoice
		//payment needs to have any extra costs 
		
		return new ModelAndView("redirect:/realiseReservation/" + payment.getId());
	}
}
