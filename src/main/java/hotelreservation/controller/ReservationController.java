package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.ReservationCheckout;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.GuestService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.RoomService;


@Controller
public class ReservationController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private GuestService guestService;

	@Autowired
	private InvoiceService invoiceService;
	
	// TODO figure out what is this for since I thought that dates worked prior to having this copied and pasted in.
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}

	@PreAuthorize("hasAuthority('createReservation')")
	@GetMapping(value = { "/reservation", "/reservation/{id}", "/reservation/start/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> endDate, @PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("reservation", new Reservation());
			model.addAttribute("room", new Room());
		} else {
			Reservation reservation = bookingService.getReservation(id);
			model.addAttribute("reservation", reservation == null ? new Reservation() : reservation);

			Map<Room, List<RoomRate>> roomRatesAsMap = new HashMap<>();

			for (RoomRate roomRate : reservation.getRoomRates()) {
				if (roomRatesAsMap.containsKey(roomRate.getRoom())) {
					roomRatesAsMap.get(roomRate.getRoom()).add(roomRate);
				} else {
					roomRatesAsMap.put(roomRate.getRoom(), new ArrayList<RoomRate>(Arrays.asList(roomRate)));
				}
			}

			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
		}

		model.addAttribute("startDate", startDate.isPresent() ? startDate.get() : LocalDate.now());
		model.addAttribute("endDate", endDate.isPresent() ? endDate.get() :  LocalDate.now());

		if (startDate.isPresent() && endDate.isPresent()) {
			Map<Room, List<RoomRate>> roomRatesAsMap = roomService.getRoomRatesAsMap(startDate.get(), endDate.get());
			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
		}
		
		LocalDate asDateStart = startDate.isPresent() ? startDate.get() : LocalDate.now();
		LocalDate asDateEnd = endDate.isPresent() ? endDate.get() : asDateStart.plus(1, ChronoUnit.DAYS);
 
		Map<LocalDate, List<RoomRate>> roomRatesAsMapByDates = roomService.getRoomRatesPerDate(asDateStart, asDateEnd);
		model.addAttribute("roomRatesAsMapByDates", roomRatesAsMapByDates);
		
		List<RoomRate> roomRates = roomRatesAsMapByDates.get(asDateStart);
		List<Integer> collect = roomRates.stream().map(r -> r.getRoom().getRoomNumber()).collect(Collectors.toList());
		model.addAttribute("roomNumbers", collect);
		
		return "reservation";
	}

	@GetMapping(value = { "/realiseReservation/{id}" })
	@PreAuthorize("hasAuthority('realiseReservation')")
	public String getRealiseReservation(@PathVariable Optional<Integer> id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);

		Guest guest = new Guest();
		guest.setContact(new Contact());
		guest.setIdentification(new Identification());

		model.addAttribute("guest", guest);
		model.addAttribute("idTypes", IdType.values());

		List<String> countries = new ArrayList<>();

		for (String countryCode : Locale.getISOCountries()) {
			countries.add(new Locale("", countryCode).getDisplayCountry());
		}

		Collections.sort(countries);
		model.addAttribute("countries", countries);

		List<RoomRate> roomRates = reservation.getRoomRates();
		model.addAttribute("total", roomRates.stream().mapToInt(RoomRate::getValue).sum());
		model.addAttribute("currency", roomRates.get(0).getCurrency().toString());

		return "realiseReservation";
	}

	@GetMapping(value = { "/cancelReservation/{id}" })
	@PreAuthorize("hasAuthority('cancelReservation')")
	public String cancelReservation(@PathVariable Optional<Integer> id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);

		if (reservation.getReservationStatus().equals(ReservationStatus.Cancelled)) {
			//To be implemented
		} else {
			model.addAttribute("reservationCancellation", new ReservationCancellation());
		}

		return "cancelReservation";
	}

	@GetMapping(value = {"/dashboard"})
	@PreAuthorize("hasAuthority('viewReservationDashBoard')")
	public String getReservationDashBoard(Model model) {
		log.info("loading dashboard");

		model.addAttribute("upComingReservations", bookingService.getReservationsByStatus(ReservationStatus.UpComing));
		model.addAttribute("inProgressReservations", bookingService.getReservationsByStatus(ReservationStatus.InProgress));

		log.info("dashboard ready");
		return "reservationDashBoard";
	}

	@GetMapping(value = { "/checkoutReservation/{id}" })
	@PreAuthorize("hasAuthority('checkoutReservation')")
	public String checkoutReservation(@PathVariable Optional<Integer> id, Model model) {
		log.info("loading checkout Reservation");

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);
		model.addAttribute("reservationCheckout", new ReservationCheckout());
		model.addAttribute("reservationPayments", invoiceService.getAllPaymentsForReservation(reservation));
		model.addAttribute("charges", invoiceService.getAllCharges());
		model.addAttribute("reservationCharge",  new ReservationChargeDTO());
		model.addAttribute("reservationCharges",  invoiceService.getAllReservationChargesForAReservation(reservation));

		List<RoomRate> roomRates = reservation.getRoomRates();
		model.addAttribute("total", roomRates.stream().mapToInt(RoomRate::getValue).sum());
		model.addAttribute("currency", roomRates.get(0).getCurrency().toString());

		log.info("checkout Reservation ready");
		return "checkoutReservation";
	}

	/*
	 * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	@PostMapping("/addOccupant/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView addOccupant(@Valid @ModelAttribute Guest guest, @PathVariable Optional<Integer> reservationId) {
		guestService.saveIdentification(guest.getIdentification());
		guestService.saveContact(guest.getContact());
		guestService.saveGuest(guest);
		
		Reservation reservation = bookingService.getReservation(reservationId);
		
		if(reservation.getOccupants() == null) {
			List<Guest> occupants = new ArrayList<>();
			occupants.add(guest);
			reservation.setOccupants(occupants);
		} else {
			reservation.getOccupants().add(guest);
		}
		bookingService.saveReservation(reservation);

		return new ModelAndView("redirect:/realiseReservation/" + reservationId.get());
	}

	@PostMapping("/realiseReservation/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView realiseReservation(@PathVariable Optional<Integer> reservationId) {
		Reservation reservation = bookingService.getReservation(reservationId);

		if (reservation.getReservationStatus().equals(ReservationStatus.UpComing) || reservation.getReservationStatus().equals(ReservationStatus.InProgress)) {
			// TODO can't realise a cancelled or in progress reservation
		} else { 
			// TODO return some erro message
		}

		bookingService.realiseReservation(reservation);

		return new ModelAndView("redirect:/dashboard");
	}

	@PostMapping("/reservation")
	@PreAuthorize("hasAuthority('createReservation')")
	public ModelAndView saveReservation(@Valid @ModelAttribute Reservation reservation, @RequestParam List<Long> roomRateIds) { //TODO ensure this is provided
		bookingService.saveReservationAndValidateRoomRates(reservation, roomRateIds);
		return new ModelAndView("redirect:/dashboard");
	}

	// TODO only super-admin type user should be able to fully delete a reservation. Move to super admin controller? 
	@DeleteMapping(value = "/reservationDelete/{id}")
	@PreAuthorize("hasAuthority('deleteReservation')")
	public ModelAndView deleteReservation(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			// bookingService.deleteUser(new Long(id.get()));
		}
		return new ModelAndView("redirect:/reservation");
	}

	@PostMapping(value = "/deleteContact/{guestId}/reservationId/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView deleteGuest(@PathVariable Optional<Integer> guestId,  @PathVariable Optional<Integer> reservationId) {
		if (guestId.isPresent()) {
			log.info("deleting guest: " + guestId.get() + " from reservation: " + reservationId);
			Guest guestToDelete = guestService.getGuestById(guestId.get());
			Reservation resFromDB = bookingService.getReservation(reservationId);
			
			if(!resFromDB.getOccupants().contains(guestToDelete)) {
				throw new IllegalArgumentException("Guest: " + guestToDelete.getId() + " is not in the list of Guests for reservation: " + resFromDB.getId());
			} else {
				//TODO can't delete the last occupant or make it so that you can't realise a reservation without an occupant
				resFromDB.getOccupants().remove(guestToDelete);
				guestService.deleteGuest(guestId);
			}
		}
		
		log.info("can't delete nonexistant guest");

		return new ModelAndView("redirect:/realiseReservation/" + reservationId.get());
	}
	
	@PostMapping("/cancelReservation/{reservationID}")
	@PreAuthorize("hasAuthority('cancelReservation')")
	public ModelAndView cancelReservation(@Valid @ModelAttribute ReservationCancellation reservationCancellation, @PathVariable Optional<Integer> reservationID) {
		Reservation resFromDB = bookingService.getReservation(reservationID);

		reservationCancellation.setReservation(resFromDB);
		bookingService.cancelReservation(reservationCancellation);

		return new ModelAndView("redirect:/dashboard");
	}

	@PostMapping("/fulfillReservation/{reservationID}")
	@PreAuthorize("hasAuthority('fulfillReservation')")
	public ModelAndView fulfillReservation(@PathVariable Optional<Integer> reservationID) {
		bookingService.fulfillReservation(reservationID);

		return new ModelAndView("redirect:/dashboard");
	}
}