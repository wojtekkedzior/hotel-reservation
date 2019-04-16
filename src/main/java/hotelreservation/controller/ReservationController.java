package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.Utils;
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
	
	@Autowired
	private Utils dateConvertor;

	// TODO figure out what is this for since I thought that dates worked prior to having this copied and pasted in.
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}

	@PreAuthorize("hasAuthority('createReservation')")
	@RequestMapping(value = { "/reservation", "/reservation/{id}", "/reservation/start/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate, @PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("reservation", new Reservation());
			model.addAttribute("room", new Room());
		} else {
			Reservation reservation = bookingService.getReservation(id);
			model.addAttribute("reservation", reservation == null ? new Reservation() : reservation);

			Map<Room, List<RoomRate>> roomRatesAsMap = new HashMap<Room, List<RoomRate>>();

			for (RoomRate roomRate : reservation.getRoomRates()) {
				if (roomRatesAsMap.containsKey(roomRate.getRoom())) {
					roomRatesAsMap.get(roomRate.getRoom()).add(roomRate);
				} else {
					roomRatesAsMap.put(roomRate.getRoom(), new ArrayList<RoomRate>(Arrays.asList(roomRate)));
				}
			}

			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
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

		// TODO if there are no rooms available we neeed to display something usefull to the user.

		Map<Date, List<RoomRate>> roomRatesAsMapByDates = new HashMap<Date, List<RoomRate>>();
		
		Date asDateStart = dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 13));
		Date asDateEnd = dateConvertor.asDate(LocalDate.of(2018, Month.APRIL, 15));
		
		for (RoomRate roomRate : roomService.getRoomRates(asDateStart, asDateEnd)) {
			if(roomRatesAsMapByDates.containsKey(roomRate.getDay())) {
				roomRatesAsMapByDates.get(roomRate.getDay()).add(roomRate);
				
			} else {
				List<RoomRate> roomRates = new ArrayList<>();
				roomRates.add(roomRate);
				roomRatesAsMapByDates.put(roomRate.getDay(), roomRates);
			}
		}

		model.addAttribute("roomRatesAsMapByDates", roomRatesAsMapByDates);
		
		return "reservation";
	}

	@RequestMapping(value = { "/realiseReservation/{id}" }, method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('realiseReservation')")
	public String getRealiseReservation(@PathVariable Optional<Integer> id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);

		Guest guest = new Guest();
		guest.setContact(new Contact());
		guest.setIdentification(new Identification());

		model.addAttribute("guest", guest);
		model.addAttribute("idTypes", IdType.values());

		List<String> countries = new ArrayList<String>();

		for (String countryCode : Locale.getISOCountries()) {
			countries.add(new Locale("", countryCode).getDisplayCountry());
		}

		Collections.sort(countries);
		model.addAttribute("countries", countries);

		List<RoomRate> roomRates = reservation.getRoomRates();
		model.addAttribute("total", roomRates.stream().mapToInt(n -> n.getValue()).sum());
		model.addAttribute("currency", roomRates.get(0).getCurrency().toString());

		return "realiseReservation";
	}

	@RequestMapping(value = { "/cancelReservation/{id}" })
	@PreAuthorize("hasAuthority('cancelReservation')")
	public String cancelReservation(@PathVariable Optional<Integer> id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute("reservation", reservation);

		if (reservation.getReservationStatus().equals(ReservationStatus.Cancelled)) {

		} else {
			model.addAttribute("reservationCancellation", new ReservationCancellation());
		}

		return "cancelReservation";
	}

	@RequestMapping(value = {"/dashboard"})
	@PreAuthorize("hasAuthority('viewReservationDashBoard')")
	public String getReservationDashBoard(Model model) {
		log.info("loading dashboard");

		model.addAttribute("upComingReservations", bookingService.getReservationsByStatus(ReservationStatus.UpComing));
		model.addAttribute("inProgressReservations", bookingService.getReservationsByStatus(ReservationStatus.InProgress));

		log.info("dashboard ready");
		return "reservationDashBoard";
	}

	@RequestMapping(value = { "/checkoutReservation/{id}" })
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
		model.addAttribute("total", roomRates.stream().mapToInt(n -> n.getValue()).sum());
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
	public ModelAndView saveReservation(@Valid @ModelAttribute Reservation reservation) {
		bookingService.saveReservation(reservation);

		return new ModelAndView("redirect:/dashboard");
	}

	// TODO only super-admin type user should be able to fully delete a reservation. Move to super admin controller? 
	@RequestMapping(value = "/reservationDelete/{id}", method = RequestMethod.DELETE)
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