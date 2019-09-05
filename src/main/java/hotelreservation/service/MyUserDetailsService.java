package hotelreservation.service;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		
		List<GrantedAuthority> grantedAuthorities = getAuthorities(user.getRoles());
		user.getRoles().stream().forEach(t -> grantedAuthorities.add(new SimpleGrantedAuthority(t.getName())));
		
		if(log.isDebugEnabled()) {
			log.info("User: {} found and has the following authorities: {}", userName, grantedAuthorities);
		}
		
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), user.isEnabled(), true, true, true, grantedAuthorities);
	}

	private List<GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {
		List<Privilege> collection = new ArrayList<>();
		
		roles.stream()
		.filter(t -> t.getPrivileges() != null)
		.forEach(t -> collection.addAll(t.getPrivileges()));

		List<String> privileges = new ArrayList<>();
		collection.stream().forEach(t -> privileges.add(t.getName()));
		return privileges;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		privileges.stream().forEach(t -> authorities.add(new SimpleGrantedAuthority(t)));
		return authorities;
	}
}