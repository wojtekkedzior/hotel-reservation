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

import hotelreservation.model.finance.Payment;
import hotelreservation.service.InvoiceService;


@Controller
public class InvoiceController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private InvoiceService invoiceService;
	
	@RequestMapping(value = { "/invoice/{id}" }, method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('retrieveInvoice')")
	public String getInvoice(@PathVariable Optional<Integer> id, Model model) {

		return "invoice";
	}
	
	@PostMapping("/createPayment/{id}")
	@PreAuthorize("hasAuthority('createPayment')")
	public ModelAndView createPayment(@ModelAttribute Payment payment, @PathVariable Optional<Integer> id, BindingResult bindingResult) {
		log.info("creating paymeny for reservation: " + id);

		//TODO still need to understand why the id field of payment is getting set to the value of the id field which is meant for the reservation.
		//obviously it's in the naming convention
		payment.setId(0);
		
		invoiceService.savePayment(payment);

		//TODO use credit card in reservation?
		// gather all payment details and call createpayment.  if successful generate invoice and show 'show invoice' button to download/display the invoice
		//payment needs to have any extra costs 
		
		return new ModelAndView("redirect:/checkoutReservation/" + id.get());
	}
}
