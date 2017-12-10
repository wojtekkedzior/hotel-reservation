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

import hotelreservation.model.User;
import hotelreservation.model.UserType;
import hotelreservation.service.UserService;

@Controller
public class UsersController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/user", "/user/{id}" })
	public String users(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			model.addAttribute("user", new User());
		} else {
			User user = userService.getUserById(id);
			if(user == null) {
				model.addAttribute("user", new User());
			} else {
				model.addAttribute("user", user);
			}
		}

		model.addAttribute("userType", new UserType());
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("userTypes", userService.getAllUserTypes());
		return "user";
	}
	
	@RequestMapping(value = { "/userType", "/userType/{id}" })
	public String userTypes(Model model, @PathVariable Optional<Integer> id) {

		if(!id.isPresent()) {
			model.addAttribute("userType", new UserType());
		} else {
			UserType userType = userService.getUserTypeById(id);
			if(userType == null) {
				model.addAttribute("userType", new User());
			} else {
				model.addAttribute("userType", userType);
			}
		}
		
		model.addAttribute("user", new User());
		
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("userTypes", userService.getAllUserTypes());

		return "user";
	}
	
	@PostMapping("/adduser")
	public ModelAndView addUser(@ModelAttribute User user, BindingResult bindingResult) {
		userService.createUser(user);
		return new ModelAndView("redirect:/user/" + user.getId());
	}

	@PostMapping("/addUserType")
	public ModelAndView addAUserType(@ModelAttribute UserType userType, BindingResult bindingResult) {
		userService.createUserType(userType);
		return new ModelAndView("redirect:/userType/" + userType.getId());
	}
}