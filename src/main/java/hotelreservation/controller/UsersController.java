package hotelreservation.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.service.UserService;

@Controller
public class UsersController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;

	@GetMapping(value = { "/user", "/user/{id}" })
	@PreAuthorize("hasAuthority('createUser')")
	public String getUsers(@PathVariable Optional<Integer> id, Model model) {
		if(!id.isPresent()) {
			User user = new User();
			model.addAttribute("user", user);
		} else {
			User user = userService.getUserById(id.get());
			if(user == null) {
				model.addAttribute("user", new User());
			} else {
				model.addAttribute("user", user);
			}
			log.info("getting user: {}", user);
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
	public ModelAndView addUser(@Valid @ModelAttribute User user) {
		log.info("creating user: {}", user);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Authenticated user: {} is creating: {}", authentication, user);
		
		userService.saveUser(user, authentication.getName());
		return new ModelAndView("redirect:/user/" + user.getId());
	}

	/*
	 * ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	@DeleteMapping(value="/userDelete/{id}")
	@PreAuthorize("hasAuthority('deleteUser')")
	public ModelAndView deleteUser(@PathVariable Optional<Integer> id) {
		log.info("deleting user: {}", id);
		if(id.isPresent()) {
			User userById = userService.getUserById(id.get());
			userService.deleteUser(userById);
		} 
		return new ModelAndView("redirect:/user");
	}
}