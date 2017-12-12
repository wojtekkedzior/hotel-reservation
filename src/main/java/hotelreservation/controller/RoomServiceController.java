package hotelreservation.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.DateConvertor;
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
	
	@Autowired 
	private DateConvertor dateConvertor;
	

	@RequestMapping(value={"/amenity", "/amenity/{id}"})
	public String addAmenityModel(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("amenity", new Amenity());
		} else {
			Amenity amenityById = roomService.getAmenityById(new Long(id.get()));
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
			AmenityType amenityTypeById = roomService.getAmenityTypeById(new Long(id.get()));
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

	@RequestMapping(value={"/room", "room/{id}"})
	public String roomModel(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("room", new Room()); 
		} else {
			Room room = roomService.getGetRoomById(new Long(id.get()));
			if(room == null) {
				model.addAttribute("room", new Room()); 
			} else {
				model.addAttribute("room", room);
			}
		}
		
		model.addAttribute("roomType", new RoomType());
		
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("amenities", roomService.getRoomAmenities());
		model.addAttribute("statuses", roomService.getAllStatuses());
		return "room";
	}
	
	@RequestMapping(value={"/roomType", "/roomType/{id}"})
	public String roomTypeModel(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("roomType", new RoomType());
		} else {
			RoomType roomType = roomService.getRoomTypeById(new Long(id.get()));
			if(roomType == null) {
				model.addAttribute("roomType", new RoomType());
			} else {
				model.addAttribute("roomType", roomType); 
			}
		}
		
		model.addAttribute("room", new Room());
		
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("roomTypes", roomService.getAllRoomTypes());
		model.addAttribute("amenities", roomService.getRoomAmenities());
		model.addAttribute("statuses", roomService.getAllStatuses());
		return "room";
	}
	
	@RequestMapping(value={"/roomRate", "/roomRate/{id}"})
	public String addRoomRateModel(Model model, @PathVariable Optional<Integer> id) throws ParseException {
		if(!id.isPresent()) {
			model.addAttribute("roomRate", new RoomRate());
		} else {
			RoomRate roomRate = roomService.getRoomRateById(new Long(id.get()));
			if(roomRate == null) {
				model.addAttribute("roomRate", new RoomRate());
			} else {
				model.addAttribute("roomRate", roomRate); 
			}
		}
		
		Calendar cal = new GregorianCalendar();
		System.err.println("cal:" + cal.toString());
		cal.roll(Calendar.DAY_OF_YEAR, 2); 
		
		Date asDate = dateConvertor.asDate(LocalDate.now());
		Date asDate2 = dateConvertor.asDate(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)));
				
				
		List<RoomRate> availableRoomRates = roomService.getAvailableRoomRatesForAllRooms(asDate, asDate2);
		model.addAttribute("roomRates", availableRoomRates);
		
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("currencies", Currency.values());
		return "roomRate";
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
		return new ModelAndView("redirect:/roomType/" + roomType.getId());
	}
	
	@PostMapping("/addRoom") 
	public ModelAndView addRoom(@ModelAttribute Room room, BindingResult bindingResult) {
		roomService.createRoom(room);
		return new ModelAndView("redirect:/room/" + room.getId());
	}
	
	@PostMapping("/addRoomRate") 
	public ModelAndView addRoomRate(@ModelAttribute RoomRate roomRate, BindingResult bindingResult) {
		roomService.createRoomRate(roomRate);
		return new ModelAndView("redirect:/roomRate/" + roomRate.getId());
	}
	
	@RequestMapping(value="/amenityDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteAminity(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			roomService.deleteAmenity(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/amenity");
	}
	
	@RequestMapping(value="/amenityTypeDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteAminityType(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			roomService.deleteAmenityType(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/amenity");
	}
	
	@RequestMapping(value="/roomDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteRoom(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			roomService.deleteRoom(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/room");
	}
	
	@RequestMapping(value="/roomTypeDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteRoomType(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			roomService.deleteRoomType(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/room");
	}
	
	@RequestMapping(value="/roomRateDelete/{id}", method=RequestMethod.DELETE)
	public ModelAndView deleteRoomRate(@PathVariable Optional<Integer> id) {
		if(id.isPresent()) {
			roomService.deleteRoomRate(new Long(id.get()));
		} 
		return new ModelAndView("redirect:/room");
	}
}
