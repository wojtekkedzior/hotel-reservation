package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@RequestMapping(value = { "/reservation", "/reservation/start/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate, Model model) {
		model.addAttribute("reservation", new Reservation());
		
		System.err.println(startDate);
		System.err.println(endDate);

		// TODO move to properties file
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		model.addAttribute("startDate", startDate.isPresent() ? formatter.format(startDate.get()) : "2018-03-09");
		model.addAttribute("endDate", endDate.isPresent() ? formatter.format(endDate.get()) : "2018-03-20");

		model.addAttribute("room", new Room());
		model.addAttribute("reservation", new Reservation());
		
		if(startDate.isPresent() && endDate.isPresent()) {
			model.addAttribute("roomRatesPerRoom", roomService.getRoomRatesAsMap(startDate.get(), endDate.get()));
		}
		
		return "reservation";
	}

	@RequestMapping(value = "/editReservation/{id}")
	public String getReservationModel(Model model, @PathVariable int id) {
		model.addAttribute("reservation", bookingService.getReservation(id));
		return "reservation";
	}

	@PostMapping("/reservation")
	public ModelAndView addAmenityType(@ModelAttribute Reservation reservation, BindingResult bindingResult, RedirectAttributes redir) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservation");
		redir.addFlashAttribute("id", 1);

		bookingService.createReservation(reservation);
		return modelAndView;
	}
}