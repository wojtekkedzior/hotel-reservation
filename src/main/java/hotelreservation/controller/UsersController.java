package hotelreservation.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.model.ui.UserDTO;
import hotelreservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final UserService userService;

    @GetMapping(value = {"/user", "/user/{id}"})
    @PreAuthorize("hasAuthority('createUser')")
    public String getUsers(@PathVariable(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("user", new User());
        } else {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
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
                .role(userDTO.getRole())
                .enabled(true)
                .build();

        User createdUser = userService.saveUser(userToBeCreated, authentication.getName());
        return new ModelAndView("redirect:/user/" + createdUser.getId());
    }

    /*
     * ---------------------------------------------------------------------------------------------------------------------------
     */

    @DeleteMapping(value = "/userDelete/{id}")
    @PreAuthorize("hasAuthority('deleteUser')")
    public ModelAndView deleteUser(@PathVariable(required = false) Long id) {
        if (id != null) {
            log.info("deleting user: {}", id);
            userService.deleteUser(id);
        }

        return new ModelAndView("redirect:/user");
    }
}