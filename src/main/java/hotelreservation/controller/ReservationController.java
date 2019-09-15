package hotelreservation.controller;

import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.*;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.ui.GuestDTO;
import hotelreservation.model.ui.ReservationCancellationDTO;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ReservationController {
	private static final String RESERVATION = "reservation";
	private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
	private static final String REDIRECT_RESERVATION = "redirect:/reservation";

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
	private UserService userService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}

	@PreAuthorize("hasAuthority('createReservation')")
	@GetMapping(value = { "/reservation", "/reservation/{id}", "/reservation/start/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> endDate, @PathVariable(required = false) Long id, Model model) {
		if (id == null) {
			model.addAttribute(RESERVATION, new Reservation());
			model.addAttribute("room", new Room());
		} else {

			Map<Room, List<RoomRate>> roomRatesAsMap = new HashMap<>();

			try {
				Reservation reservation = bookingService.getReservation(id);
				model.addAttribute(RESERVATION, reservation);

				for (RoomRate roomRate : reservation.getRoomRates()) {
					roomRatesAsMap.computeIfAbsent(roomRate.getRoom(), k -> new ArrayList<>()).add(roomRate);
				}

			}catch (NotFoundException e) {
				model.addAttribute(RESERVATION, new Reservation());
			}

			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
		}

		model.addAttribute("startDate", startDate.orElseGet(LocalDate::now));
		model.addAttribute("endDate", endDate.orElseGet(LocalDate::now));

		if (startDate.isPresent() && endDate.isPresent()) {
			Map<Room, List<RoomRate>> roomRatesAsMap = roomService.getRoomRatesAsMap(startDate.get(), endDate.get());
			model.addAttribute("roomRatesPerRoom", roomRatesAsMap);
		}
		
		LocalDate asDateStart = startDate.orElseGet(LocalDate::now);
		LocalDate asDateEnd = endDate.orElseGet(() -> asDateStart.plus(1, ChronoUnit.DAYS));
 
		Map<LocalDate, List<RoomRate>> roomRatesAsMapByDates = roomService.getRoomRatesPerDate(asDateStart, asDateEnd);
		model.addAttribute("roomRatesAsMapByDates", roomRatesAsMapByDates);
		
		List<RoomRate> roomRates = roomRatesAsMapByDates.get(asDateStart);
		List<Integer> collect = roomRates.stream().map(r -> r.getRoom().getRoomNumber()).collect(Collectors.toList());
		model.addAttribute("roomNumbers", collect);
		
		return RESERVATION;
	}

	@GetMapping(value = { "/realiseReservation/{id}" })
	@PreAuthorize("hasAuthority('realiseReservation')")
	public String getRealiseReservation(@PathVariable Long id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute(RESERVATION, reservation);

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
	public String cancelReservation(@PathVariable Long id, Model model) {
		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute(RESERVATION, reservation);

		if (reservation.getReservationStatus().equals(ReservationStatus.CANCELLED)) {
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

		model.addAttribute("upComingReservations", bookingService.getReservationsByStatus(ReservationStatus.UP_COMING));
		model.addAttribute("inProgressReservations", bookingService.getReservationsByStatus(ReservationStatus.IN_PROGRESS));

		log.info("dashboard ready");
		return "reservationDashBoard";
	}

	@GetMapping(value = { "/checkoutReservation/{id}" })
	@PreAuthorize("hasAuthority('checkoutReservation')")
	public String checkoutReservation(@PathVariable Long id, Model model) {
		log.info("loading checkout Reservation");

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute(RESERVATION, reservation);
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
	public ModelAndView addOccupant(@Valid @ModelAttribute GuestDTO guestDTO, @PathVariable Long reservationId) {

		if (reservationId == null) {
			new ModelAndView(REDIRECT_RESERVATION);
		}

		Guest guest = Guest.builder()
				.firstName(guestDTO.getFirstName())
				.lastName(guestDTO.getLastName())
				.description(guestDTO.getDescription())
				.contact(guestDTO.getContact())
				.identification(guestDTO.getIdentification())
				.build();

		guestService.saveIdentification(guest.getIdentification());
		guestService.saveContact(guest.getContact());
		guest = guestService.saveGuest(guest);

		//TODO get res first before saving any guest stuff
		Reservation reservation = bookingService.getReservation(reservationId);
		
		if(reservation.getOccupants() == null) {
			List<Guest> occupants = new ArrayList<>();
			occupants.add(guest);
			reservation.setOccupants(occupants);
		} else {
			reservation.getOccupants().add(guest);
		}
		bookingService.saveReservation(reservation);

		return new ModelAndView("redirect:/realiseReservation/" + reservationId);
	}

	@PostMapping("/realiseReservation/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView realiseReservation(@PathVariable Long reservationId) {
		Reservation reservation = bookingService.getReservation(reservationId);

		if (reservation.getReservationStatus().equals(ReservationStatus.UP_COMING)) {
			bookingService.realiseReservation(reservation);
		} else {
			log.info("Can't realise a cancelled or In Progress reservation: {} with status: {}", reservation.getId(), reservation.getReservationStatus());
		}
		return new ModelAndView(REDIRECT_DASHBOARD);
	}

	@PostMapping("/reservation")
	@PreAuthorize("hasAuthority('createReservation')")
	public ModelAndView saveReservation(@Valid @ModelAttribute Reservation reservation, @RequestParam List<Long> roomRateIds) { //TODO ensure this is provided
		bookingService.saveReservationAndValidateRoomRates(reservation, roomRateIds);
		return new ModelAndView(REDIRECT_DASHBOARD);
	}

	@DeleteMapping(value = "/reservationDelete/{reservationId}")
	@PreAuthorize("hasAuthority('deleteReservation')")
	public ModelAndView deleteReservation(@PathVariable Long reservationId) {
		Reservation resFromDB = bookingService.getReservation(reservationId);
		bookingService.deleteReservation(resFromDB);
		log.info("deleting reservation: {}", reservationId);
		//TODO throw new IllegalArgumentException("only a super user can do this");
		return new ModelAndView(REDIRECT_RESERVATION);
	}

	@PostMapping(value = "/deleteContact/{guestId}/reservationId/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView deleteGuest(@PathVariable Long guestId,  @PathVariable Long reservationId) {

		if (reservationId == null) {
			new ModelAndView(REDIRECT_RESERVATION);
		}

		log.info("deleting guest: {} from reservation: {}", guestId, reservationId);
		Guest guestToDelete = guestService.getGuestById(guestId);
		Reservation resFromDB = bookingService.getReservation(reservationId);

		if(!resFromDB.getOccupants().contains(guestToDelete)) {
			throw new IllegalArgumentException("Guest: " + guestToDelete.getId() + " is not in the list of Guests for reservation: " + resFromDB.getId());
		} else {
			//TODO can't delete the last occupant or make it so that you can't realise a reservation without an occupant
			resFromDB.getOccupants().remove(guestToDelete);
			guestService.deleteGuest(guestId);
		}

		log.info("can't delete nonexistant guest");

		return new ModelAndView("redirect:/realiseReservation/" + reservationId);
	}
	
	@PostMapping("/cancelReservation/{reservationID}")
	@PreAuthorize("hasAuthority('cancelReservation')")
	public ModelAndView cancelReservation(@Valid @ModelAttribute ReservationCancellationDTO reservationCancellationDTO, @PathVariable Long reservationID) {
		Reservation reservation = bookingService.getReservation(reservationID);

		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User userFromDb = userService.getUserByUserName(principal.getUsername());

		ReservationCancellation cancellation = ReservationCancellation.builder()
				.reservation(reservation)
				.reason(reservationCancellationDTO.getReason())
				.cancelledBy(userFromDb)
				.cancelledOn(LocalDateTime.now())
				.build();

		cancellation.setReservation(reservation);
		bookingService.cancelReservation(cancellation);

		return new ModelAndView(REDIRECT_DASHBOARD);
	}

	@PostMapping("/fulfillReservation/{reservationID}")
	@PreAuthorize("hasAuthority('fulfillReservation')")
	public ModelAndView fulfillReservation(@PathVariable Long reservationID) {
		bookingService.fulfillReservation(reservationID);

		return new ModelAndView(REDIRECT_DASHBOARD);
	}
}