package hotelreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.service.BookingService;
import hotelreservation.service.RoomService;
import hotelreservation.service.UserService;

@Controller
public class AdminController {

  @Autowired
  private UserService userService;
  
  @Autowired 
  private RoomService roomService;
  
  @Autowired
  private BookingService bookingService;

  @RequestMapping("/admin")
  public String hello(Model model) {
	  model.addAttribute("userTypes", userService.getAllUserTypes());
	  model.addAttribute("roomTypes", roomService.getAllRoomTypes());
	  model.addAttribute("amenityTypes", roomService.getAllAmenityTypes());
	  model.addAttribute("users", userService.getUsers());
	  model.addAttribute("reservations", bookingService.getAllReservations());
      return "admin";
  }
}