package hotelreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Currency;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.service.RoomService;

@Controller
public class RoomServiceController {

	@Autowired
	private RoomService roomService;

	@RequestMapping("/addAmenity")
	public String addAmenityModel(Model model) {
		model.addAttribute("amenity", new Amenity());
		model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
		return "addAmenity";
	}

	@RequestMapping("/addAmenityType")
	public String addAmenityTypeModel(Model model) {
		model.addAttribute("amenityType", new AmenityType());
		return "addAmenityType";
	}

	@RequestMapping("/addRoomType")
	public String addRoomTypeModel(Model model) {
		model.addAttribute("roomType", new RoomType());
		return "addRoomType";
	}
	
	@RequestMapping("/addRoom")
	public String addRoomModel(Model model) {
		model.addAttribute("room", new Room());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("amenities", roomService.getRoomAmenities());
		model.addAttribute("statuses", roomService.getAllStatuses());
		return "addRoom";
	}
	
	@RequestMapping("/addRoomRate")
	public String addRoomRateModel(Model model) {
		model.addAttribute("roomRate", new RoomRate());
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("currencies", Currency.values());
		return "addRoomRate";
	}

	@PostMapping("/addAmenityType")
	public ModelAndView addAmenityType(@ModelAttribute AmenityType amenityType, BindingResult bindingResult) {
		System.err.println(amenityType);
		roomService.createAmenityType(amenityType);
		return new ModelAndView("redirect:/admin");
	}

	@PostMapping("/addAmenity")
	public ModelAndView addAmenity(@ModelAttribute Amenity amenity, BindingResult bindingResult) {
		System.err.println(amenity);
		roomService.createAmenity(amenity);
		return new ModelAndView("redirect:/admin");
	}

	@PostMapping("/addRoomType")
	public ModelAndView addRoomType(@ModelAttribute RoomType roomType, BindingResult bindingResult) {
		roomService.createRoomType(roomType);
		return new ModelAndView("redirect:/admin");
	}
	
	@PostMapping("/addRoom") 
	public ModelAndView addRoom(@ModelAttribute Room room, BindingResult bindingResult) {
		roomService.createRoom(room);
		return new ModelAndView("redirect:/admin");
	}
	
	@PostMapping("/addRoomRate") 
	public ModelAndView addRoomRate(@ModelAttribute RoomRate roomRate, BindingResult bindingResult) {
		roomService.createRoomRate(roomRate);
		return new ModelAndView("redirect:/admin");
	}
}
