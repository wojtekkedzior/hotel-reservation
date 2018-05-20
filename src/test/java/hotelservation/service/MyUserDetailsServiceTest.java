package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;
import hotelreservation.service.MyUserDetailsService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class MyUserDetailsServiceTest {
	
	@Autowired
	private MyUserDetailsService service;
	
	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PrivilegeRepo privilegeRepo;
	
	private Role role;
	private User user;
	private Privilege priv1;
	private Privilege priv2;
	
	private List<Privilege> privileges;

	@Before
	public void setup() {
		role = new Role("receptionistRole", "desc", true);
		roleRepo.save(role);
		
		user = new User("receptionist", "bobalina");
		Collection<Role> roles = new ArrayList<Role>();
		roles.add(role);
		user.setRoles(roles);
		user.setPassword("password");
		
		userRepo.save(user);
		
		priv1 = new Privilege("priv1");
		priv2 = new Privilege("priv2");
		
		privilegeRepo.save(priv1);
		privilegeRepo.save(priv2);
		
		privileges = new ArrayList<Privilege>();
		privileges.add(priv1);
		privileges.add(priv2);
	}
	
	@Test (expected = UsernameNotFoundException.class)
	public void testEmptyUserName() {
		service.loadUserByUsername("nonExistentUser");
	}
	
	@Test
	public void testUserWithoutPrivileges() {
		UserDetails userDetails = service.loadUserByUsername("receptionist");
		assertEquals(userDetails.getUsername(), "receptionist");
		assertEquals(userDetails.getPassword(), "password");
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		assertTrue(userDetails.isEnabled());
		
		assertEquals(1, userDetails.getAuthorities().size());
	}
	
	@Test
	public void testUserWithPrivileges() {
		role.setPrivileges(privileges);
		roleRepo.save(role);
		
		UserDetails userDetails = service.loadUserByUsername("receptionist");
		assertEquals(userDetails.getUsername(), "receptionist");
		assertEquals(userDetails.getPassword(), "password");
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		assertTrue(userDetails.isEnabled());
		
		assertEquals(3, userDetails.getAuthorities().size());
	}
	
	@Test
	public void testUserWithPrivilegesDisabledUser() {
		role.setPrivileges(privileges);
		
		user.setEnabled(false);
		userRepo.save(user);
		
		UserDetails userDetails = service.loadUserByUsername("receptionist");
		assertEquals(userDetails.getUsername(), "receptionist");
		assertEquals(userDetails.getPassword(), "password");
		assertTrue(userDetails.isAccountNonExpired());
		assertTrue(userDetails.isAccountNonLocked());
		assertTrue(userDetails.isCredentialsNonExpired());
		
		assertFalse(userDetails.isEnabled());
		assertEquals(3, userDetails.getAuthorities().size());
	}
}