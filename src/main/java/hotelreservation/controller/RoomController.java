package hotelreservation.controller;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.enums.Currency;
import hotelreservation.service.RoomService;

@Controller
public class RoomController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomService roomService;

	@GetMapping(value = { "/amenity", "/amenity/{id}" })
	@PreAuthorize("hasAuthority('createAmenity')")
	public String getAmenityModel(Model model, @PathVariable Optional<Integer> id) {
		log.info("Getting Amenity with id: " + id);
		
		if (!id.isPresent()) {
			model.addAttribute("amenity", new Amenity());
			model.addAttribute("amenityType", new AmenityType());
		} else {
			Amenity amenityById = roomService.getAmenityById(id.get());
			model.addAttribute("amenity", amenityById);
			model.addAttribute("amenityType", amenityById.getAmenityType());
		}

		addAmenityAttributes(model);

		return "amenity";
	}

	@GetMapping(value = { "/amenityType", "/amenityType/{id}" })
	@PreAuthorize("hasAuthority('createAmenityType')")
	public String getAmenityTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute("amenityType", new AmenityType());
		} else {
			AmenityType amenityTypeById = roomService.getAmenityTypeById(id.get());
			model.addAttribute("amenityType", amenityTypeById == null ? new AmenityType() : amenityTypeById);
		}

		model.addAttribute("amenity", new Amenity());
		addAmenityAttributes(model);

		return "amenity";
	}

	private void addAmenityAttributes(Model model) {
		model.addAttribute("amenities", roomService.getAllAmenities());
		model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
	}

	@GetMapping(value = { "/room", "room/{id}" })
	@PreAuthorize("hasAuthority('createRoom')")
	public String roomModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("room", new Room());
		} else {
			Room room = roomService.getRoomById(id.get());
			model.addAttribute("room", room == null ? new Room() : room);
		}

		model.addAttribute("roomType", new RoomType());
		addRoomAttribbutes(model);

		return "room";
	}

	@GetMapping(value = { "/roomType", "/roomType/{id}" })
	@PreAuthorize("hasAuthority('createRoomType')")
	public String roomTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute("roomType", new RoomType());
		} else {
			RoomType roomType = roomService.getRoomTypeById(id.get());
			model.addAttribute("roomType", roomType == null ? new RoomType() : roomType);
		}

		model.addAttribute("room", new Room());
		addRoomAttribbutes(model);

		return "room";
	}

	private void addRoomAttribbutes(Model model) {
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("amenities", roomService.getRoomAmenities());
		model.addAttribute("statuses", roomService.getAllStatuses());
	}

	@GetMapping(value = { "/roomRate", "/roomRate/{id}" })
	@PreAuthorize("hasAuthority('createRoomRate')")
	public String addRoomRateModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("roomRate", new RoomRate());
		} else {
			RoomRate roomRate = roomService.getRoomRateById(id.get());
			model.addAttribute("roomRate", roomRate == null ? new RoomRate() : roomRate);
		}

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, 30);

		LocalDate start = LocalDate.now();
		LocalDate end = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

		model.addAttribute("roomRates", roomService.getRoomRates(start, end));
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("currencies", Currency.values());
		return "roomRate";
	}

	@PostMapping("/addAmenityType")
	@PreAuthorize("hasAuthority('createAmenityType')")
	public ModelAndView addAmenityType(@Valid @ModelAttribute AmenityType amenityType) {
		AmenityType createAmenityType = roomService.saveAmenityType(amenityType);
		return new ModelAndView("redirect:/amenityType/" + createAmenityType.getId());
	}

	//TODO lots of these post create duplicate records. need to handle update
	@PostMapping("/addAmenity")
	@PreAuthorize("hasAuthority('createAmenity')")
	public ModelAndView addAmenity(@Valid @ModelAttribute Amenity amenity) {
		Amenity createAmenity = roomService.saveAmenity(amenity);
		return new ModelAndView("redirect:/amenity/" + createAmenity.getId());
	}

	@PostMapping("/addRoomType")
	@PreAuthorize("hasAuthority('createRoomType')")
	public ModelAndView addRoomType(@Valid @ModelAttribute RoomType roomType) {
		roomService.saveRoomType(roomType);
		return new ModelAndView("redirect:/roomType/" + roomType.getId());
	}

	@PostMapping("/addRoom")
	@PreAuthorize("hasAuthority('createRoom')")
	public ModelAndView addRoom(@Valid @ModelAttribute Room room) {
		roomService.saveRoom(room);
		return new ModelAndView("redirect:/room/" + room.getId());
	}

	@PostMapping("/addRoomRate")
	@PreAuthorize("hasAuthority('createRoomRate')")
	public ModelAndView addRoomRate(@Valid @ModelAttribute RoomRate roomRate) {
		roomService.saveRoomRate(roomRate);
		return new ModelAndView("redirect:/roomRate/" + roomRate.getId());
	}

	@DeleteMapping(value = "/amenityDelete/{id}")
	@PreAuthorize("hasAuthority('deleteAmenity')")
	public ModelAndView deleteAminity(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteAmenity(id.get());
		}
		return new ModelAndView("redirect:/amenity");
	}

	@DeleteMapping(value = "/amenityTypeDelete/{id}")
	@PreAuthorize("hasAuthority('deleteAmenityType')")
	public ModelAndView deleteAminityType(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteAmenityType(id.get());
		}
		return new ModelAndView("redirect:/amenity");
	}

	@DeleteMapping(value = "/roomDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoom')")
	public ModelAndView deleteRoom(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomById(id.get());
		}
		return new ModelAndView("redirect:/room");
	}

	@DeleteMapping(value = "/roomTypeDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoomType')")
	public ModelAndView deleteRoomType(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomType(Long.valueOf(id.get()));
		}
		return new ModelAndView("redirect:/room");
	}

	@DeleteMapping(value = "/roomRateDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoomRate')")
	public ModelAndView deleteRoomRate(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomRate(id.get());
		}
		// TODO wha thappens with all the histortical bookings that refer to this room rate?
		return new ModelAndView("redirect:/roomRate");
	}
}