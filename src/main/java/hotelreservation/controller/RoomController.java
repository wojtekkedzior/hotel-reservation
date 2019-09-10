package hotelreservation.controller;

import hotelreservation.model.*;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.ui.AmenityDTO;
import hotelreservation.model.ui.AmenityTypeDTO;
import hotelreservation.model.ui.RoomRateDTO;
import hotelreservation.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
public class RoomController {

	private static final String AMENITY = "amenity";
	private static final String AMENITY_TYPE = "amenityType";
	private static final String ROOM_RATE = "roomRate";
	private static final String ROOM_TYPE = "roomType";
	private static final String ROOM = "room";
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomService roomService;

	@GetMapping(value = { "/amenity", "/amenity/{id}" })
	@PreAuthorize("hasAuthority('createAmenity')")
	public String getAmenityModel(Model model, @PathVariable Optional<Integer> id) {
		log.info("Getting Amenity with id: {}", id);

		if (!id.isPresent()) {
			model.addAttribute(AMENITY, new Amenity());
			model.addAttribute(AMENITY_TYPE, new AmenityType());
		} else {
			Amenity amenityById = roomService.getAmenityById(id.get());
			model.addAttribute(AMENITY, amenityById);
			model.addAttribute(AMENITY_TYPE, amenityById.getAmenityType());
		}

		addAmenityAttributes(model);

		return AMENITY;
	}

	@GetMapping(value = { "/amenityType", "/amenityType/{id}" })
	@PreAuthorize("hasAuthority('createAmenityType')")
	public String getAmenityTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute(AMENITY_TYPE, new AmenityType());
		} else {
			AmenityType amenityTypeById = roomService.getAmenityTypeById(id.get());
			model.addAttribute(AMENITY_TYPE, amenityTypeById == null ? new AmenityType() : amenityTypeById);
		}

		model.addAttribute(AMENITY, new Amenity());
		addAmenityAttributes(model);

		return AMENITY;
	}

	private void addAmenityAttributes(Model model) {
		model.addAttribute("amenities", roomService.getAllAmenities());
		model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
	}

	@GetMapping(value = { "/room", "room/{id}" })
	@PreAuthorize("hasAuthority('createRoom')")
	public String roomModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute(ROOM, new Room());
		} else {
			Room room = roomService.getRoomById(id.get());
			model.addAttribute(ROOM, room == null ? new Room() : room);
		}

		model.addAttribute(ROOM_TYPE, new RoomType());
		addRoomAttributes(model);

		return ROOM;
	}

	@GetMapping(value = { "/roomType", "/roomType/{id}" })
	@PreAuthorize("hasAuthority('createRoomType')")
	public String roomTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if (!id.isPresent()) {
			model.addAttribute(ROOM_TYPE, new RoomType());
		} else {
			RoomType roomType = roomService.getRoomTypeById(id.get());
			model.addAttribute(ROOM_TYPE, roomType == null ? new RoomType() : roomType);
		}

		model.addAttribute(ROOM, new Room());
		addRoomAttributes(model);

		return ROOM;
	}

	private void addRoomAttributes(Model model) {
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("amenities", roomService.getRoomAmenities());
		model.addAttribute("statuses", roomService.getAllStatuses());
	}

	@GetMapping(value = { "/roomRate", "/roomRate/{id}" })
	@PreAuthorize("hasAuthority('createRoomRate')")
	public String addRoomRateModel(@PathVariable Optional<Integer> id, Model model) {
		if (!id.isPresent()) {
			model.addAttribute(ROOM_RATE, new RoomRate());
		} else {
			RoomRate roomRate = roomService.getRoomRateById(id.get());
			model.addAttribute(ROOM_RATE, roomRate == null ? new RoomRate() : roomRate);
		}

		model.addAttribute("roomRates", roomService.getRoomRates(LocalDate.now(), LocalDate.now().plus(30, ChronoUnit.DAYS)));
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("currencies", Currency.values());
		return ROOM_RATE;
	}

	@PostMapping("/addAmenityType")
	@PreAuthorize("hasAuthority('createAmenityType')")
	public ModelAndView addAmenityType(@Valid @ModelAttribute AmenityTypeDTO amenityTypeDTO) {
		AmenityType createAmenityType = new AmenityType(amenityTypeDTO.getName(), amenityTypeDTO.getDescription());
		createAmenityType = roomService.saveAmenityType(createAmenityType);

		log.info("created AmenityType: {}", createAmenityType);
		return new ModelAndView("redirect:/amenityType/" + createAmenityType.getId());
	}

	//TODO lots of these post create duplicate records. need to handle update
	@PostMapping("/addAmenity")
	@PreAuthorize("hasAuthority('createAmenity')")
	public ModelAndView addAmenity(@Valid @ModelAttribute AmenityDTO amenityDTO) {

		Amenity createAmenity = Amenity.builder()
				.description(amenityDTO.getDescription())
				.amenityType(amenityDTO.getAmenityType())
				.name(amenityDTO.getName())
				.build();

		createAmenity = roomService.saveAmenity(createAmenity);
		log.info("created Amenity: {}", createAmenity);
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
	public ModelAndView addRoomRate(@Valid @ModelAttribute RoomRateDTO roomRateDTO) {

		RoomRate roomRate = RoomRate.builder()
				.currency(roomRateDTO.getCurrency())
				.day(roomRateDTO.getDay())
				.description(roomRateDTO.getDescription())
				.room(roomRateDTO.getRoom())
				.value(roomRateDTO.getValue())
				.build();

		roomRate = roomService.saveRoomRate(roomRate);
		log.info("Created a RoomRate: {}", roomRate);
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