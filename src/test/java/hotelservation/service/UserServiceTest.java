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
import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PrivilegeRepo privilegeRepo;
	
	@Before
	public void setup() {
	}

	@Test
	public void whenFindByName_thenReturnEmployee() {
		Role userType = new Role("UserType name", "UserTypeDescription", true);
		userService.createRole(userType);

		assertTrue(userService.getAllRoles().size() == 1);
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
		assertTrue(target.size() == 4);
		
		assertTrue(target.contains(superAdminUserType));
		assertTrue(target.contains(adminUserType));
		assertTrue(target.contains(managerUserType));
		assertTrue(target.contains(receptionUserType));
	}
	
	@Test
	public void testCRUDUser() {
		User user = new User("username", "name");
		user.setPassword("password");
		userService.createUser(user);
		
		assertEquals(1, userService.getAllUsers().size());
		
		user.setFirstName("updatedFirstName");
		
		assertEquals(1, userService.getAllUsers().size());
		assertEquals("updatedFirstName", userService.getAllUsers().get(0).getFirstName());
		
		userService.deleteUser(user);
		assertEquals(0, userService.getAllUsers().size());
	}
	
	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testCreateUserWithoutPassword() {
		userService.createUser(new User());
	}
	
	@Test
	public void testCRUDRole() {
		Role role = new Role();
		userService.createRole(role);
		
		assertEquals(1, userService.getAllRoles().size());
		
		role.setName("updatedName");
		
		assertEquals(1, userService.getAllRoles().size());
		assertEquals("updatedName", userService.getAllRoles().get(0).getName());
		
		userService.deleteRole(role);
		assertEquals(0, userService.getAllUsers().size());
	}
	
	@Test
	public void testCRUDPrivileges() {
		Privilege privilege = new Privilege();
		userService.createPrivilege(privilege);
		
		assertEquals(1, userService.getAllPrivileges().size());
		
		privilege.setName("updatedName");
		
		assertEquals(1, userService.getAllPrivileges().size());
		assertEquals("updatedName", userService.getAllPrivileges().get(0).getName());
		
		privilegeRepo.delete(privilege);
		assertEquals(0, userService.getAllPrivileges().size());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentUserType() {
		userService.getUserById(99);
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentRoleRate() {
		userService.getRoleById(99);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentUser() {
		userService.deleteUser(new User());
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRole() {
		userService.deleteRole(new Role());
	}
}