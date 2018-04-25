package hotelreservation.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//TODO move to new controller which will handle security and other bits and pieces
	@RequestMapping("/")
	@PreAuthorize("hasAnyRole('ROLE_RECEPTIONIST', 'ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
	public ModelAndView redirectIndexToHOme(HttpServletRequest request) {
	     if (request.isUserInRole("ROLE_ADMIN")) {
//	            return "redirect:/events/";
	        }
//	        return "redirect:/"; 
	        
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservationDashBoard");
		return modelAndView;
	}
	
}
