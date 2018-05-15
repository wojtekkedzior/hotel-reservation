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

	@RequestMapping("/") 
	//TODO this needs to be secured
	public ModelAndView redirectIndexToHome(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/reservationDashBoard");
		return modelAndView;
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/signin")
	public String signin() {
		return "login";
	}
}
