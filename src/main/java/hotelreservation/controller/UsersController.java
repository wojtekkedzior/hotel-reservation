package hotelreservation.controller;

import java.util.Optional;

import javax.validation.Valid;

import hotelreservation.model.ui.UserDTO;
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
	public ModelAndView addUser(@Valid @ModelAttribute UserDTO userDTO) {
		log.info("creating user: {}", userDTO);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Authenticated user: {} is creating: {}", authentication, userDTO);

		User userToBeCreated = User.builder()
				.firstName(userDTO.getFirstName())
				.lastName(userDTO.getLastName())
				.userName(userDTO.getUserName())
				.password(userDTO.getPassword())
				.roles(userDTO.getRoles())
				.enabled(true)
				.build();

		User createdUser = userService.saveUser(userToBeCreated, authentication.getName());
		return new ModelAndView("redirect:/user/" + createdUser.getId());
	}

	/*
	 * ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	@DeleteMapping(value="/userDelete/{id}")
	@PreAuthorize("hasAuthority('deleteUser')")
	public ModelAndView deleteUser(@PathVariable Optional<Integer> id) {
		log.info("deleting user: {}", id);

		id.ifPresent(userId -> {
			userService.deleteUser(userService.getUserById(userId));
		});

		return new ModelAndView("redirect:/user");
	}
}