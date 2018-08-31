package hotelreservation.controller;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.Utils;
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

	@Autowired
	private Utils dateConvertor;

	@RequestMapping(value = { "/amenity", "/amenity/{id}" })
	@PreAuthorize("hasAuthority('createAmenity')")
	public String addAmenityModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute("amenity", new Amenity());
		} else {
			Amenity amenityById = roomService.getAmenityById(new Long(id.get()));
			model.addAttribute("amenity", amenityById == null ? new Amenity() : amenityById);
		}

		model.addAttribute("amenityType", new AmenityType());
		addAmenityAttributes(model);

		return "amenity";
	}

	@RequestMapping(value = { "/amenityType", "/amenityType/{id}" })
	@PreAuthorize("hasAuthority('createAmenityType')")
	public String addAmenityTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute("amenityType", new AmenityType());
		} else {
			AmenityType amenityTypeById = roomService.getAmenityTypeById(new Long(id.get()));
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

	@RequestMapping(value = { "/room", "room/{id}" })
	@PreAuthorize("hasAuthority('createRoom')")
	public String roomModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("room", new Room());
		} else {
			Room room = roomService.getRoomById(new Long(id.get()));
			model.addAttribute("room", room == null ? new Room() : room);
		}

		model.addAttribute("roomType", new RoomType());
		addRoomAttribbutes(model);

		return "room";
	}

	@RequestMapping(value = { "/roomType", "/roomType/{id}" })
	@PreAuthorize("hasAuthority('createRoomType')")
	public String roomTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute("roomType", new RoomType());
		} else {
			RoomType roomType = roomService.getRoomTypeById(new Long(id.get()));
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

	@RequestMapping(value = { "/roomRate", "/roomRate/{id}" })
	@PreAuthorize("hasAuthority('createRoomRate')")
	public String addRoomRateModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute("roomRate", new RoomRate());
		} else {
			RoomRate roomRate = roomService.getRoomRateById(new Long(id.get()));
			model.addAttribute("roomRate", roomRate == null ? new RoomRate() : roomRate);
		}

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, 30);

		Date start = dateConvertor.asDate(LocalDate.now());
		Date end = dateConvertor.asDate(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));

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

	@RequestMapping(value = "/amenityDelete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteAmenity')")
	public ModelAndView deleteAminity(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteAmenity(new Long(id.get()));
		}
		return new ModelAndView("redirect:/amenity");
	}

	@RequestMapping(value = "/amenityTypeDelete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteAmenityType')")
	public ModelAndView deleteAminityType(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteAmenityType(new Long(id.get()));
		}
		return new ModelAndView("redirect:/amenity");
	}

	@RequestMapping(value = "/roomDelete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteRoom')")
	public ModelAndView deleteRoom(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomById(new Long(id.get()));
		}
		return new ModelAndView("redirect:/room");
	}

	@RequestMapping(value = "/roomTypeDelete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteRoomType')")
	public ModelAndView deleteRoomType(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomType(new Long(id.get()));
		}
		return new ModelAndView("redirect:/room");
	}

	@RequestMapping(value = "/roomRateDelete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteRoomRate')")
	public ModelAndView deleteRoomRate(@PathVariable Optional<Integer> id) {
		if (id.isPresent()) {
			roomService.deleteRoomRate(new Long(id.get()));
		}
		// TODO wha thappens with all the histortical bookings that refer to this room rate?
		return new ModelAndView("redirect:/roomRate");
	}
}