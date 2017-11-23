package hotelreservation.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.model.Reservation;
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
//		
		LocalDate start = LocalDate.of(2017, Month.JANUARY, 1);
		LocalDate end = LocalDate.of(2017, Month.JANUARY, 6);
		
		List<RoomRate> availableRoomRatesForRoom = roomService.getAvailableRoomRatesForRoom(start, end);
		model.addAttribute("roomRates", availableRoomRatesForRoom);
//		model.addAttribute("currencies", Currency.values());
		return "addReservation";
	}
//
//	@PostMapping("/addAmenityType")
//	public ModelAndView addAmenityType(@ModelAttribute AmenityType amenityType, BindingResult bindingResult) {
//		System.err.println(amenityType);
//		roomService.createAmenityType(amenityType);
//		return new ModelAndView("redirect:/admin");
//	}
}
