package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;

@Controller
public class ReservationController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;
	
	//TODO figure out what is this for since I thought that dates worked prior to having this copied and pasted in.
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}

	@RequestMapping(value = { "/reservation", "/reservation/{id}", "/reservation/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate, @PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("reservation", new Reservation());
			model.addAttribute("room", new Room());
		} else {
			Reservation reservation = bookingService.getReservation(id);
			model.addAttribute("reservation", reservation == null ? new Reservation() : reservation);
		}

		// TODO move to properties file
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		// TODO hard-coded for now
		model.addAttribute("startDate", startDate.isPresent() ? formatter.format(startDate.get()) : "2018-03-09");
		model.addAttribute("endDate", endDate.isPresent() ? formatter.format(endDate.get()) : "2018-03-20");

		if (startDate.isPresent() && endDate.isPresent()) {
			model.addAttribute("roomRatesPerRoom", roomService.getRoomRatesAsMap(startDate.get(), endDate.get()));
		}

		model.addAttribute("reservations", bookingService.getAllReservations());
		
		return "reservation";
	}
	
	@RequestMapping(value = {"/realiseReservation/{id}"})
	public String getRealiseReservation(@PathVariable Optional<Integer> id, Model model) {

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);
		
		return "";
	}

	@PostMapping("/reservation")
	public ModelAndView addAmenityType(@ModelAttribute Reservation reservation, BindingResult bindingResult, RedirectAttributes redir) {
		//TODO need to make use of the binding results (in all Post handlers)
		System.err.println(bindingResult); // need to handle binding results
		
		bookingService.createReservation(reservation);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservation");
		return modelAndView;
	}
	
	@RequestMapping(value="/reservationDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteUser(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
//			bookingService.deleteUser(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/reservation");
	}
}