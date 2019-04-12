package hotelreservation.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import hotelreservation.model.Privilege;
import hotelreservation.model.Role;

@Service("userDetailsServiceForTest")
@ActiveProfiles("test")
@Transactional
public class MyUserDetailsServiceForTest implements UserDetailsService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
//		User user = userRepo.findByUserName(userName);
		
		log.info("Login attempt for user: " + userName);
//		
//		if (user == null) { 
//			log.error("User: " + userName + " doesn't exist");
//			throw new UsernameNotFoundException("Username: " + userName + " was not found");
//		}

//		List<GrantedAuthority> grantedAuthorities = getAuthorities(user.getRoles());
//		user.getRoles().stream().forEach(t -> grantedAuthorities.add(new SimpleGrantedAuthority(t.getName())));
		
		log.info("User: " + userName + " found");
//
//		if(log.isDebugEnabled()) {
//			log.info("User: " + userName + " found and has the following authorities: " + grantedAuthorities);
//		}
		
//		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), user.isEnabled(), true, true, true, grantedAuthorities);
		return new org.springframework.security.core.userdetails.User("asd", "asd", true, true, true, true, new ArrayList<>());
	}

	private List<GrantedAuthority> getAuthorities(Collection<Role> roles) {
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {
		List<Privilege> collection = new ArrayList<>();
		
		roles.stream().filter(t -> {
			return t.getPrivileges() != null ? true : false;
		}).forEach(t -> collection.addAll(t.getPrivileges()));
		
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