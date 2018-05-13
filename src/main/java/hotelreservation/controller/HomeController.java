package hotelreservation.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//TODO move to new controller which will handle security and other bits and pieces
	@RequestMapping("/")
//	@PreAuthorize("hasAnyRole('receptionist', 'manager', 'admin')")
	public ModelAndView redirectIndexToHome(HttpServletRequest request) {
	     if (request.isUserInRole("ROLE_ADMIN")) {
//	            return "redirect:/events/";
	        }
//	        return "redirect:/"; 
	        
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservationDashBoard");
		return modelAndView;
	}
	
	
	  // Login form
	  @RequestMapping("/login")
	  public String login() {
	    return "login";
	  }

	  // Login form with error
	  @RequestMapping("/login-error.html")
	  public String loginError(Model model) {
	    model.addAttribute("loginError", true);
	    return "login.html";
	  }
	
}
