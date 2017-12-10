package hotelreservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.model.Reservation;
import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@Controller
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@RequestMapping("/admin")
	public String hello(Model model) {
		model.addAttribute("userTypes", userService.getAllUserTypes());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("users", userService.getUsers());
		model.addAttribute("reservations", bookingService.getAllReservations());

		List<Reservation> findByStartDate = bookingService.getReservationsStartingToday();
		model.addAttribute("reservationsStartingToday", findByStartDate);
		return "admin";
	}
}