package hotelreservation.service;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("userDetailsService")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MyUserDetailsService implements UserDetailsService {
	
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String userName) {
		log.info("Login attempt for user: {}", userName);
		User user = userService.getUserByUserName(userName);
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
			return new ArrayList<>();
		}
		return role.getPrivileges().stream()
				.map(Privilege::getName).collect(Collectors.toList()).stream()
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
}