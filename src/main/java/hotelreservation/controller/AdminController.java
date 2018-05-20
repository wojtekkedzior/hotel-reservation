package hotelreservation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.model.Reservation;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;

@Controller
public class AdminController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@RequestMapping("/admin")
	@PreAuthorize("hasAuthority('viewAdmin')")
	public String getAdmin(Model model) {
		log.info("loading admin");

		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("reservations", bookingService.getAllReservations());

		List<Reservation> findByStartDate = bookingService.getReservationsStartingToday();
		model.addAttribute("reservationsStartingToday", findByStartDate);
		
		log.info("admin ready");
		return "adminOverview";
	}
	
	@RequestMapping("/")
	public String handleRoot(Model model) {
		return "redirect:/dashboard";
	}
}