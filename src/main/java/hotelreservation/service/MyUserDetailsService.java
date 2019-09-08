package hotelreservation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String userName) {
		log.info("Login attempt for user: {}", userName);
		User user = userService.getUserByName(userName);
		log.info("User: {} found. ", userName);
		
		List<GrantedAuthority> grantedAuthorities = getAuthorities(user.getRole());
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
		
		if(log.isDebugEnabled()) {
			log.info("User: {} found and has the following authorities: {}", userName, grantedAuthorities);
		}
		
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), user.isEnabled(), true, true, true, grantedAuthorities);
	}

	private List<GrantedAuthority> getAuthorities(Role role) {
		if(role.getPrivileges() == null) {
			return new ArrayList<GrantedAuthority>();
		}
		return role.getPrivileges().stream()
				.map(privilege -> privilege.getName()).collect(Collectors.toList()).stream()
				.map(privilegeName -> new SimpleGrantedAuthority(privilegeName)).collect(Collectors.toList());
	}
}