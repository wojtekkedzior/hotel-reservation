package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.service.BookingService;
import hotelreservation.service.GuestService;
import hotelreservation.service.RoomService;

@Controller
public class ReservationController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private GuestService guestService;
	

	@RequestMapping("/addReservation")
	public String addReservationModel(Model model) {
		model.addAttribute("reservation", new Reservation());
		return "addReservation";
	}
	
	@RequestMapping(value = "/editReservation/{id}") 
	public String getReservationModel(Model model, @PathVariable int id) {
		model.addAttribute("reservation", bookingService.getReservation(id));
		return "addReservation";
	} 

	//Sohuld get rid of this post. need to build up an href via js adding start and end date for a filter and hit the get method.  need to add params to it as well.
	@PostMapping("/addReservation/withDates")
	public String addReservation(Model model, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		System.err.println(startDate);
		System.err.println(endDate);

		// TODO move to properties file
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		model.addAttribute("startDate", formatter.format(startDate));
		model.addAttribute("endDate", formatter.format(endDate));

		model.addAttribute("room", new Room());
		model.addAttribute("reservation", new Reservation());
		model.addAttribute("roomRatesPerRoom", roomService.getRoomRatesForAllRoomsAsMap(startDate, endDate));
 
		return "addReservation";
	} 

	@PostMapping("/addReservation")
	public ModelAndView addAmenityType(@ModelAttribute Reservation reservation, BindingResult bindingResult, RedirectAttributes redir) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/addReservation");
		redir.addFlashAttribute("id", 1);
 
		bookingService.createReservation(reservation);
		return modelAndView;
	}
}