package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
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
	
	//TODO figure out what is this for since I thought that dates worked prior to having this copied and pasted in.
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}
	
	//TODO move to new controller which will handle security and other bits and pieces
	@RequestMapping("/")
	public ModelAndView redirectIndexToHOme() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/home");
		return modelAndView;
	}

	@RequestMapping(value = { "/reservation", "/reservation/{id}", "/reservation/start/{startDate}/end/{endDate}" })
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
		model.addAttribute("startDate", startDate.isPresent() ? formatter.format(startDate.get()) : "2018-04-09");
		model.addAttribute("endDate", endDate.isPresent() ? formatter.format(endDate.get()) : "2018-04-20");

		if (startDate.isPresent() && endDate.isPresent()) {
			Map<Room, List<RoomRate>> roomRatesAsMap = roomService.getRoomRatesAsMap(startDate.get(), endDate.get());
			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
		}

		//TODO if there are no rooms available we neeed to display something usefull to the user.
		
		return "reservation";
	}
	
	
	
	@RequestMapping(value = {"/realiseReservation/{id}"})
	public String getRealiseReservation(@PathVariable Optional<Integer> id, Model model) {

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);
		
		Guest guest = new Guest();
		guest.setContact(new Contact());
		guest.setIdentification(new Identification());
		
		model.addAttribute("guest", guest);
		model.addAttribute("idTypes", IdType.values());
		
		return "realiseReservation";
	}
	
	@RequestMapping(value = {"/cancelReservation/{id}"})
	public String cancelReservation(@PathVariable Optional<Integer> id, Model model) {

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);
		
		if(reservation.getReservationStatus().equals(ReservationStatus.Cancelled)) {
			
		} else {
			model.addAttribute("reservationCancellation", new ReservationCancellation());
		}
		
		return "cancelReservation";
	}
	
	@RequestMapping(value = {"reservationDashBoard"})
	public String getReservationDashBoard(Model model) {
		
		model.addAttribute("upComingReservations", bookingService.getReservationsByStatus(ReservationStatus.UpComing));
		model.addAttribute("inProgressReservations", bookingService.getReservationsByStatus(ReservationStatus.InProgress));
		
		return "reservationDashBoard";
	}

	
	
	
	@PostMapping("/addOccupant") 
	public ModelAndView addOccupant(@ModelAttribute Reservation reservation, Guest guest, BindingResult bindingResult) {
		//TODO the guest ID is also set because it matches the id field name on the reservation. Is there a way to exclude that?
		guest.setId(0);
		
		//TODO need to make use of the binding results (in all Post handlers)
		System.err.println(bindingResult); // need to handle binding results
		
		guestService.saveContact(guest.getContact());
		guestService.saveIdentification(guest.getIdentification());
		guestService.saveGuest(guest);
		
		Reservation reservation2 = bookingService.getReservation(reservation.getId());
		reservation2.getOccupants().add(guest);
		bookingService.createReservation(reservation2);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/realiseReservation/" + reservation.getId());
		return modelAndView;
	}
	
	@PostMapping("/realiseReservation") 
	public ModelAndView realiseReservation(@ModelAttribute Reservation reservation, BindingResult bindingResult) {
		Reservation reservation2 = bookingService.getReservation(reservation.getId());
		
		if(reservation2.getReservationStatus().equals(ReservationStatus.UpComing) || reservation2.getReservationStatus().equals(ReservationStatus.InProgress)) {
			//TODO can't realise a cancelled or in progress reservation
		} else {
			//TODO return some erro message
		}
		
		
		reservation2.setReservationStatus(ReservationStatus.InProgress);
		
		
		bookingService.saveReservation(reservation2);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservationDashBoard");
		return modelAndView;
	}

	@PostMapping("/reservation")
	public ModelAndView saveReservation(@ModelAttribute Reservation reservation, BindingResult bindingResult, RedirectAttributes redir) {
		//TODO need to make use of the binding results (in all Post handlers)
		System.err.println(bindingResult); // need to handle binding results
		
		bookingService.createReservation(reservation);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservation");
		return modelAndView;
	}
	
	//TODO only super-admin type user should be able to fully delete a reservation
	@RequestMapping(value="/reservationDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteReservation(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
//			bookingService.deleteUser(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/reservation");
	}
	
	@RequestMapping(value="/deleteContact/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteGuest(@ModelAttribute Reservation reservation, @PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			//remove it from the reservation first.
			//need to check if the guest actually existsi
			//can't have no guests.
			
			Guest guestToDelete = null;
			
			Reservation resFromDB = bookingService.getReservation(reservation.getId());
			//TODO ugly 
			for (Guest occupant : resFromDB.getOccupants()) {
				if(occupant.getId() == id.get()) {
					guestToDelete = occupant;
				}
			}
			
			if(guestToDelete != null) {
				resFromDB.getOccupants().remove(guestToDelete);
			}
			
			guestService.deleteGuest(id);
		} 
		
		return new ModelAndView("redirect:/realiseReservation/" + reservation.getId());
	}
	
	@PostMapping("/cancelReservation/{reservationID}")
	public ModelAndView cancelReservation(@ModelAttribute ReservationCancellation reservationCancellation, @PathVariable Optional<Integer> reservationID) {
		Reservation resFromDB = bookingService.getReservation(reservationID);
		
		reservationCancellation.setId(0); //TODO need to figure out why the ID is being set. in this case the reservation ID is also placed into the ReservationCancellation
		reservationCancellation.setReservation(resFromDB);
		
		bookingService.cancelReservation(resFromDB, reservationCancellation);
		
		System.err.println("reservationCancellation: " + reservationCancellation.getId());
		System.err.println(reservationID);
		
		
		return new ModelAndView("redirect:/reservationDashBoard");
	}
}