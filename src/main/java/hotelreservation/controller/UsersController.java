package hotelreservation.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.service.UserService;

@Controller
public class UsersController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/user", "/user/{id}" })
	@PreAuthorize("hasAuthority('createUser')")
	public String getUsers(Model model, @PathVariable Optional<Integer> id) {
		if(!id.isPresent()) {
			User user = new User();
			model.addAttribute("user", user);
		} else {
			User user = userService.getUserById(id);
			if(user == null) {
				model.addAttribute("user", new User());
			} else {
				model.addAttribute("user", user);
			}
			log.info("getting user: " + user);
		}

		model.addAttribute("role", new Role()); //TODO this will be a multi select so have to select all available roles
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("roles", userService.getAllRoles());
		
		return "user";
	}
	
	/*
	 * ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	@PostMapping("/adduser")
	@PreAuthorize("hasAuthority('createUser')")
	public ModelAndView addUser(@ModelAttribute User user,  Authentication authentication, BindingResult bindingResult) {
		//TODO check that ID == null 
		log.info("creating user: " + user);
		userService.createUser(user);
		return new ModelAndView("redirect:/user/" + user.getId());
	}

	/*
	 * ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	@RequestMapping(value="/userDelete/{id}", method=RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('deleteUser')")
	public ModelAndView deleteUser(@PathVariable Optional<Integer> id) {
		log.info("deleting user: " + id);
		if(id.isPresent()) {
			User userById = userService.getUserById(id);
			userService.deleteUser(userById);
		} 
		return new ModelAndView("redirect:/user");
	}
}