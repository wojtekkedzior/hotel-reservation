package hotelservation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hotelreservation.Application;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Before
	public void setup() {
	}

	@Test
	public void whenFindByName_thenReturnEmployee() {
		// given
		Role userType = new Role("UserType name", "UserTypeDescription", true);
		userService.createRole(userType);

		List<Role> target = userService.getAllRoles();

		assertTrue(target.size() == 1);
//		assertEquals(userType, target.get(0)); //TODO needs fixing
	}

	@Test
	public void testDisabledUser() {
		Role role = new Role("Role Name", "RoleDescription", true);
		userService.createRole(role);

		assertTrue(userService.getAllRoles().get(0).isEnabled());
		role.setEnabled(false);

		assertFalse(userService.getAllRoles().get(0).isEnabled());
	}
	
	@Test
	public void testAddAllUserTypes() {
		Role superAdminUserType = new Role("superAdmin", "superAdmin desc", true);
		Role adminUserType = new Role("admin", "admin desc", true);
		
		Role managerUserType = new Role("manager", "manager desc", true);
		Role receptionUserType = new Role("reception", "reception desc", true);
		
		userService.createRole(superAdminUserType);
		userService.createRole(adminUserType);
		
		userService.createRole(managerUserType);
		userService.createRole(receptionUserType);
		
		List<Role> target =  userService.getAllRoles();
		System.err.println(target.size());
		assertTrue(target.size() == 4);
		
		assertTrue(target.contains(superAdminUserType));
		assertTrue(target.contains(adminUserType));
		assertTrue(target.contains(managerUserType));
		assertTrue(target.contains(receptionUserType));
	}
	
	@Test
	public void testCRUDUser() {
		User user = new User("username", "name");
		userService.createUser(user);
		
		assertEquals(1, userService.getAllUsers().size());
		
		user.setFirstName("updatedFirstName");
		
		assertEquals(1, userService.getAllUsers().size());
		assertEquals("updatedFirstName", userService.getAllUsers().get(0).getFirstName());
		
		userService.deleteUser(user);
		assertEquals(0, userService.getAllUsers().size());
	}
}