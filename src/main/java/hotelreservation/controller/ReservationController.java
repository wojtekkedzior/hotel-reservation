package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hotelreservation.model.Reservation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;

@Controller
public class ReservationController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@RequestMapping("/addReservation")
	public String addReservationModel(Model model) {
		model.addAttribute("reservation", new Reservation());
		return "addReservation";
	}

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

		// LocalDate start = LocalDate.of(2017, Month.JANUARY, 1);
		// LocalDate end = LocalDate.of(2017, Month.JANUARY, 6);

		Map<Room, List<RoomRate>> roomRates = roomService.getRoomRatesForAllRooms(startDate, endDate);
		model.addAttribute("roomRatesPerRoom", roomRates);

		return "addReservation";
	}

	// //delete user
	// @RequestMapping(value="users/doDelete", method = RequestMethod.POST)
	// public String deleteUser (@RequestParam Long id) {
	//// customerDAO.delete(id);
	// return "redirect:/users";
	// }

	// @RequestMapping(value="/edit", method=RequestMethod.POST,
	// params="action=save")
	// public ModelAndView save() {}

	@PostMapping("/addReservation")
	public ModelAndView addAmenityType(@ModelAttribute Reservation reservation, BindingResult bindingResult, RedirectAttributes redir) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/addReservation");
		redir.addFlashAttribute("id", 1);

		bookingService.createReservation(reservation);
		return modelAndView;
	}
}
