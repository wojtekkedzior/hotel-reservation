package hotelreservation.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Amenity;
import hotelreservation.model.AmenityType;
import hotelreservation.model.Room;
import hotelreservation.model.RoomRate;
import hotelreservation.model.RoomType;
import hotelreservation.model.enums.Currency;
import hotelreservation.service.RoomService;

@Controller
public class RoomServiceController {

	@Autowired
	private RoomService roomService;

	@RequestMapping(value={"/amenity", "/amenity/{id}"} )
	public String addAmenityModel(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("amenity", new Amenity());
		} else {
			Amenity amenityById = roomService.getAmenityById(id);
			if(amenityById == null) {
				model.addAttribute("amenity", new Amenity());
			} else {
				model.addAttribute("amenity", amenityById);
			}
		}
		
		model.addAttribute("amenityType", new AmenityType()); 
		
		model.addAttribute("amenities", roomService.getAllAmenities());
		model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
		return "amenity";
	}

	@RequestMapping(value={"/amenityType", "/amenityType/{id}"})
	public String addAmenityTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("amenityType", new AmenityType()); 
		} else {
			AmenityType amenityTypeById = roomService.getAmenityTypeById(id);
			if(amenityTypeById == null) {
				model.addAttribute("amenityType", new AmenityType()); 
			} else {
				model.addAttribute("amenityType", amenityTypeById);
			}
		}
		
		model.addAttribute("amenity", new Amenity());
		
		model.addAttribute("amenities", roomService.getAllAmenities());
		model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
		
		return "amenity";
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
		AmenityType createAmenityType = roomService.createAmenityType(amenityType);
		return new ModelAndView("redirect:/amenityType/"+createAmenityType.getId());
	}

	@PostMapping("/addAmenity")
	public ModelAndView addAmenity(@ModelAttribute Amenity amenity, BindingResult bindingResult) {
		Amenity createAmenity = roomService.createAmenity(amenity);
		return new ModelAndView("redirect:/amenity/"+createAmenity.getId());
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
