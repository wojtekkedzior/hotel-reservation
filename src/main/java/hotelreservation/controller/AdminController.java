package hotelreservation.controller;

import hotelreservation.model.Reservation;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.service.BookingService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {
	private final RoomService roomService;
	private final BookingService bookingService;
	private final InvoiceService invoiceService;

	@GetMapping("/admin")
	@PreAuthorize("hasAuthority('viewAdmin')")
	public String getAdmin(Model model) {
		log.info("loading admin");

		model.addAttribute("numberOfRooms", roomService.getRoomsCount());
		model.addAttribute("numberOfReservations", bookingService.getReservationCount());
		model.addAttribute("numberOfRoomRates", roomService.getRoomRateCount());

		List<Reservation> findByStartDate = bookingService.getReservationsStartingToday();
		model.addAttribute("reservationsStartingToday", findByStartDate);
		
		model.addAttribute("reservationsUpcoming", bookingService.getReservationsByStatus(ReservationStatus.UP_COMING).size());
		List<Reservation> reservationsInProgress = bookingService.getReservationsByStatus(ReservationStatus.IN_PROGRESS);
		model.addAttribute("reservationsCheckingOutToday", reservationsInProgress);
		
		//TODO handle different currencies
		model.addAttribute("dueCharges", invoiceService.getTotalOfOutstandingCharges(reservationsInProgress));
		
		log.info("admin ready");
		return "adminOverview";
	}
	
	@GetMapping("/")
	public String handleRoot(Model model) {
		return "redirect:/dashboard";
	}
	
	
	//A controller method to instant block all bookings going forward until manually unblokced. Admin only. in pace to rpevent if a mistaken 
	//rate has been entered and/or an employee makes a mistake or lowers the price on purpose.
}