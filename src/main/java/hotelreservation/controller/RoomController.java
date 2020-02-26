package hotelreservation.controller;

import hotelreservation.model.*;
import hotelreservation.model.enums.Currency;
import hotelreservation.model.ui.*;
import hotelreservation.service.RoomRateService;
import hotelreservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RoomController {

	private static final String AMENITY = "amenity";
	private static final String AMENITY_TYPE = "amenityType";
	private static final String ROOM_RATE = "roomRate";
	private static final String ROOM_TYPE = "roomType";
	private static final String ROOM = "room";

	private final RoomService roomService;
	private final RoomRateService roomRateService;

	@GetMapping(value = { "/amenity", "/amenity/{amenityId}" })
	@PreAuthorize("hasAuthority('createAmenity')")
	public String getAmenityModel(Model model, @PathVariable(required = false) Long amenityId) {
		log.info("Getting Amenity with id: {}", amenityId);

		if (amenityId == null) {
			model.addAttribute(AMENITY, new Amenity());
			model.addAttribute(AMENITY_TYPE, new AmenityType());
			log.info("Amenity with id: {} not found. Returning new.", amenityId);
		} else {
			Amenity amenityById = roomService.getAmenityById(amenityId);
			model.addAttribute(AMENITY, amenityById);
			model.addAttribute(AMENITY_TYPE, amenityById.getAmenityType());
			log.info("Found and returning an Amenity with id: {}", amenityId);
		}

		addAmenityAttributes(model);

		return AMENITY;
	}

	@GetMapping(value = { "/amenityType", "/amenityType/{amenityTypeId}" })
	@PreAuthorize("hasAuthority('createAmenityType')")
	public String getAmenityTypeModel(Model model, @PathVariable(required = false) Long amenityTypeId) {
		if (amenityTypeId == null) {
			model.addAttribute(AMENITY_TYPE, new AmenityType());
		} else {
			AmenityType amenityTypeById = roomService.getAmenityTypeById(amenityTypeId);
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

	@GetMapping(value = { "/room", "room/{roomId}" })
	@PreAuthorize("hasAuthority('createRoom')")
	public String roomModel(Model model, @PathVariable(required = false) Long roomId) {
		if (roomId == null) {
			model.addAttribute(ROOM, new Room());
		} else {
			Room room = roomService.getRoomById(roomId);
			model.addAttribute(ROOM, room == null ? new Room() : room);
		}

		model.addAttribute(ROOM_TYPE, new RoomType());
		addRoomAttributes(model);

		return ROOM;
	}

	@GetMapping(value = { "/roomType", "/roomType/{roomTypeId}" })
	@PreAuthorize("hasAuthority('createRoomType')")
	public String roomTypeModel(Model model, @PathVariable(required = false) Long roomTypeId) {
		if (roomTypeId == null) {
			model.addAttribute(ROOM_TYPE, new RoomType());
		} else {
			RoomType roomType = roomService.getRoomTypeById(roomTypeId);
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
	public String getRoomRate(Model model, @PathVariable(required = false) Long id) {
		if (id == null) {
			model.addAttribute(ROOM_RATE, new RoomRate());
		} else {
			RoomRate roomRate = roomRateService.getRoomRateById(id);
			model.addAttribute(ROOM_RATE, roomRate == null ? new RoomRate() : roomRate);
		}

		model.addAttribute("roomRates", roomRateService.getRoomRates(LocalDate.now(), LocalDate.now().plus(30, ChronoUnit.DAYS)));
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
	public ModelAndView addRoomType(@Valid @ModelAttribute RoomTypeDTO roomTypeDTO) {
		RoomType roomType = new RoomType(roomTypeDTO.getName(), roomTypeDTO.getDescription());
		roomType = roomService.saveRoomType(roomType);
		return new ModelAndView("redirect:/roomType/" + roomType.getId());
	}

	@PostMapping("/addRoom")
	@PreAuthorize("hasAuthority('createRoom')")
	public ModelAndView addRoom(@Valid @ModelAttribute RoomDTO roomDTO) {

		Room room = Room.builder()
				.roomNumber(roomDTO.getRoomNumber())
				.status(roomDTO.getStatus())
				.name(roomDTO.getName())
				.description(roomDTO.getDescription())
				.roomType(roomDTO.getRoomType())
				.createdOn(roomDTO.getCreatedOn())
				.createdBy(roomDTO.getCreatedBy())
				.build();

		room = roomService.saveRoom(room);
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

		roomRate = roomRateService.saveRoomRate(roomRate);
		log.info("Created a RoomRate: {}", roomRate);
		return new ModelAndView("redirect:/roomRate/" + roomRate.getId());
	}

	@DeleteMapping(value = "/amenityDelete/{id}")
	@PreAuthorize("hasAuthority('deleteAmenity')")
	public ModelAndView deleteAmenity(@PathVariable(required = false) Long id) {
		if (id != null) {
			roomService.deleteAmenity(id);
		}
		return new ModelAndView("redirect:/amenity");
	}

	@DeleteMapping(value = "/amenityTypeDelete/{id}")
	@PreAuthorize("hasAuthority('deleteAmenityType')")
	public ModelAndView deleteAmenityType(@PathVariable(required = false) Long id) {
		if (id != null) {
			roomService.deleteAmenityType(id);
		}
		return new ModelAndView("redirect:/amenity");
	}

	@DeleteMapping(value = "/roomDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoom')")
	public ModelAndView deleteRoom(@PathVariable(required = false) Long id) {
		if (id != null) {
			roomService.deleteRoomById(id);
		}
		return new ModelAndView("redirect:/room");
	}

	@DeleteMapping(value = "/roomTypeDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoomType')")
	public ModelAndView deleteRoomType(@PathVariable(required = false) Long id) {
		if (id != null) {
			roomService.deleteRoomType(id);
		}
		return new ModelAndView("redirect:/room");
	}

	@DeleteMapping(value = "/roomRateDelete/{id}")
	@PreAuthorize("hasAuthority('deleteRoomRate')")
	public ModelAndView deleteRoomRate(@PathVariable(required = false) Long id) {
		if (id != null) {
			roomRateService.deleteRoomRate(id);
		}
		// TODO wha thappens with all the histortical bookings that refer to this room rate?
		return new ModelAndView("redirect:/roomRate");
	}
}