package hotelreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.service.UserService;

@Controller
public class UsersController {
	
	  @Autowired
	  private UserService userService;

	  @RequestMapping("/addUsers") 
	  public String users(Model model) {
	      model.addAttribute("user", new User());
	      return "addUsers";
	  }
	  
	    @PostMapping("/adduser")
	    public ModelAndView addUser(@ModelAttribute User user, BindingResult bindingResult) {
	    	System.err.println(user);
	    	userService.createUser(user);
//	        return "user";
	        
//	        ModelAndView mav = new ModelAndView("addusers");
//	        mav.addObject("employee", employee);
//	        mav.addObject("date", mediumDateFormat.format(date));
//	        mav.addObject("task", new Tasks());
//	        return "admin";
	        
	        return new ModelAndView("redirect:/admin");
	    }
	    
	    @PostMapping("/addUserType")
	    public ModelAndView addAUserType(@ModelAttribute UserType userType, BindingResult bindingResult) {
	    	System.err.println(userType);
	    	userService.createUserType(userType);
	        return new ModelAndView("redirect:/admin");
	    }
}
  