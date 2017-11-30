package hotelreservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.model.AmenityType;
import hotelreservation.model.Reservation;
import hotelreservation.model.RoomType;
import hotelreservation.model.User;
import hotelreservation.model.UserType;
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

//  @RequestMapping(method = RequestMethod.GET, value = "/admin")
//  @ResponseBody
//  public ResponseEntity<?> getstats() throws FileNotFoundException, UnknownHostException {
//
//    String hostName = InetAddress.getLocalHost().getHostName();
//    Ip lastIp = repo.findByHostname(hostName);
//
//    StringBuffer res = new StringBuffer();
//    res.append("<p>Current IP: " + lastIp.getIp() + "  changed on: " + lastIp.getChangeDate() + "</p>\n");
//    res.append("<p>Total checks: " + lastIp.getChecks() + ".  Failures: " + lastIp.getFailures() + ".  Last  checked at: " + lastIp.getLastChecked() + "</p>\n");
//    
//    return new ResponseEntity<String>(res.toString(), HttpStatus.OK);
//  }
//  
  @RequestMapping("/admin")
  public String hello(Model model) {
	  List<UserType> userTypes = userService.getAllUserTypes();
      model.addAttribute("userTypes", userTypes);
      
	  List<RoomType> roomTypes = roomService.getAllRoomTypes();
      model.addAttribute("roomTypes", roomTypes);
      
	  List<AmenityType> amenityTypes = roomService.getAllAmenityTypes();
      model.addAttribute("amenityTypes", amenityTypes);
      
	  List<User> users = userService.getUsers();
      model.addAttribute("users", users);
      
	  List<Reservation> reservations = bookingService.getAllReservations();
      model.addAttribute("reservations", reservations);
       
      return "admin";
  }
  


//  @RequestMapping("/greeting")
//  public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) throws UnknownHostException {
//      String hostName = InetAddress.getLocalHost().getHostName();
//      
//      Ip myIp = repo.findByHostname(hostName);
//      model.addAttribute("myIp", myIp.getIp());
//      
//      Iterable<Ip> allCheckedIPs = repo.findAll();
//      model.addAttribute("allCheckedIPs", allCheckedIPs);
//      
//      History history = historyRepo.findOne(1l);
//      model.addAttribute("totalChecks", history.getChecks());
//      
//      long numberOfUrls = urlCheckRepo.count();
//      model.addAttribute("numberOfUrls", numberOfUrls);
//      
//      Iterable<URLHealthCheck> allUrls = urlCheckRepo.findAll();
//      model.addAttribute("urls", allUrls);
//       
//      return "greeting";
//  }
//  
//  @RequestMapping(method = RequestMethod.GET, value = "/wojtek")
//	@ResponseBody
//	public ResponseEntity<?> getOriginalImage() {
////		final HttpHeaders headers = new HttpHeaders();
////		headers.setContentType(MediaType.IMAGE_PNG);
////
////		if (originalFile == null) {
////			return new ResponseEntity<byte[]>(new byte[0], headers, HttpStatus.OK);
////		}
////		int[] data = read(originalFile);
////		BMPFormat imageFormat = new BMPFormat(data);
////		byte[] myBytes = imageFormat.getCompleteImageAsByteArray();
////
////		visitCounterService.incrementVisitCounter();
//
////		return new ResponseEntity<byte[]>(myBytes, headers, HttpStatus.CREATED);
//	  return new ResponseEntity<>(HttpStatus.OK);
//	  
//	}
}