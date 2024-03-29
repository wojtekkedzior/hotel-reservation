package hotelreservation.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Contact;
import hotelreservation.model.Guest;
import hotelreservation.model.Identification;
import hotelreservation.model.Reservation;
import hotelreservation.model.ReservationCancellation;
import hotelreservation.model.ReservationCheckout;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.User;
import hotelreservation.model.enums.IdType;
import hotelreservation.model.enums.ReservationStatus;
import hotelreservation.model.ui.GuestDTO;
import hotelreservation.model.ui.ReservationCancellationDTO;
import hotelreservation.model.ui.ReservationChargeDTO;
import hotelreservation.model.ui.ReservationDTO;
import hotelreservation.service.BookingService;
import hotelreservation.service.GuestService;
import hotelreservation.service.InvoiceService;
import hotelreservation.service.RoomRateService;
import hotelreservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
	private static final String RESERVATION = "reservation";
	private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
	private static final String REDIRECT_RESERVATION = "redirect:/reservation";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";

	private final RoomRateService roomRateService;
	private final BookingService bookingService;
	private final GuestService guestService;
	private final InvoiceService invoiceService;
	private final UserService userService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
	}

	@PreAuthorize("hasAuthority('createReservation')")
	@GetMapping(value = { "/reservation", "/reservation/start/{startDate}/end/{endDate}" })
	public String addReservationModel(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> startDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> endDate, Model model) {
		model.addAttribute(RESERVATION, new Reservation());
		model.addAttribute("room", new Room());

		LocalDate asDateStart = startDate.orElseGet(LocalDate::now);
		LocalDate asDateEnd = endDate.orElseGet(() -> asDateStart.plusDays(1));

		model.addAttribute(START_DATE, asDateStart);
		model.addAttribute(END_DATE, asDateEnd);

		Map<LocalDate, List<RoomRate>> roomRatesAsMapByDates = roomRateService.getRoomRatesPerDate(asDateStart, asDateEnd);
		model.addAttribute("roomRatesAsMapByDates", roomRatesAsMapByDates);

		model.addAttribute("roomNumbers", roomRatesAsMapByDates.get(asDateStart).stream()
				.filter(Objects::nonNull)
				.map(r -> r.getRoom().getRoomNumber())
				.collect(Collectors.toList()));

		return RESERVATION;
	}

	@PreAuthorize("hasAuthority('createReservation')")  //wrong, this should be view Reservation
	@GetMapping(value = {"/reservation/{id}"})
	public String getExistingReservation(@PathVariable Long id, Model model) {
		Map<Room, List<RoomRate>> roomRatesAsMap = new HashMap<>();

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute(RESERVATION, reservation);

		reservation.getRoomRates().stream().
				forEach(roomRate -> roomRatesAsMap.computeIfAbsent(roomRate.getRoom(), k -> new ArrayList<>()).add(roomRate));

		model.addAttribute(START_DATE, reservation.getStartDate());
		model.addAttribute(END_DATE, reservation.getEndDate());
		model.addAttribute("roomRatesPerRoom", roomRatesAsMap);

		return RESERVATION;
	}

	@PreAuthorize("hasAuthority('createReservation')")  //wrong, this should be view Reservation
	@GetMapping(value = {"/editReservation/{id}"})
	public String getExistingReservationForEdit(@PathVariable Long id, Model model) {
		Map<Room, List<RoomRate>> roomRatesAsMap = new HashMap<>();

		Reservation reservation = bookingService.getReservation(id);
		model.addAttribute(RESERVATION, reservation);

		model.addAttribute(START_DATE, reservation.getStartDate());
		model.addAttribute(END_DATE, reservation.getEndDate());
		model.addAttribute("roomRatesPerRoom", roomRatesAsMap);

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
		
		List<Reservation> inProgress = bookingService.getReservationsByStatus(ReservationStatus.IN_PROGRESS);
		model.addAttribute("inProgressReservations", inProgress);
		
		model.addAttribute("endingToday", inProgress.stream()
				.filter(res -> res.getEndDate().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
				.collect(Collectors.toList()));
		
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
		Reservation reservation = bookingService.getReservation(reservationId);

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

		if(reservation.getOccupants() == null) {
			reservation.setOccupants(Collections.singletonList(guest));
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
	public ModelAndView saveReservation(@Valid @ModelAttribute ReservationDTO reservationDTO, @RequestParam List<Long> roomRateIds) {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userService.getUserByUserName(principal.getUsername());

		Reservation reservation = Reservation.builder()
				.firstName(reservationDTO.getFirstName())
				.lastName(reservationDTO.getLastName())
				.occupants(reservationDTO.getOccupants())
				.createdBy(user)
				.startDate(reservationDTO.getStartDate())
				.endDate(reservationDTO.getEndDate())
				.reservationStatus(reservationDTO.getReservationStatus())
				.roomRates(roomRateIds.stream().map(roomRateService::getRoomRateById).collect(Collectors.toList()))
				.build();

		bookingService.saveReservation(reservation);
		return new ModelAndView(REDIRECT_DASHBOARD);
	}

	@DeleteMapping(value = "/reservationDelete/{reservationId}")
	@PreAuthorize("hasAuthority('deleteReservation')")
	public ModelAndView deleteReservation(@PathVariable Long reservationId) {
		log.info("deleting reservation: {}", reservationId);
		Reservation resFromDB = bookingService.getReservation(reservationId);
		bookingService.deleteReservation(resFromDB);
		return new ModelAndView(REDIRECT_RESERVATION);
	}

	@PostMapping(value = "/deleteContact/{guestId}/reservationId/{reservationId}")
	@PreAuthorize("hasAuthority('realiseReservation')")
	public ModelAndView deleteGuest(@PathVariable Long guestId,  @PathVariable Long reservationId) {
		log.info("deleting guest: {} from reservation: {}", guestId, reservationId);
		Guest guestToDelete = guestService.getGuestById(guestId);
		Reservation resFromDB = bookingService.getReservation(reservationId);

		if(!resFromDB.getOccupants().contains(guestToDelete)) {
			throw new IllegalArgumentException("Guest: " + guestToDelete.getId() + " is not in the list of Guests for reservation: " + resFromDB.getId());
		} else {
			//TODO can't delete the last occupant or make it so that you can't realise a reservation without an occupant
			log.info("Deleting guest: {} for reservation: {}", guestToDelete, reservationId);
			resFromDB.getOccupants().remove(guestToDelete);
			guestService.deleteGuest(guestId);
		}

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