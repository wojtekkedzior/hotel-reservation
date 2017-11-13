package hotelreservation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.service.UserService;

@Controller
public class UsersController {
	
	  @Autowired
	  private UserService userService;

	  @RequestMapping("/addUsers") 
	  public String users(Model model) {
		  List<UserType> userTypes = userService.getAllUserTypes();
	      model.addAttribute("userTypes", userTypes);
	      model.addAttribute("user", new User());
	      
	      return "addUsers";
	  }
	  
	    @PostMapping("/adduser")
	    public String addUser(@ModelAttribute User user, BindingResult bindingResult) {
	    	System.err.println(user);
	    	userService.createUser(user);
//	        return "user";
	        
//	        ModelAndView mav = new ModelAndView("addusers");
//	        mav.addObject("employee", employee);
//	        mav.addObject("date", mediumDateFormat.format(date));
//	        mav.addObject("task", new Tasks());
	        return "/admin";
	        
	    }
}
  