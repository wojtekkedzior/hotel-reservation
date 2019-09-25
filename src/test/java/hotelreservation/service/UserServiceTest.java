package hotelreservation.service;

import hotelreservation.exceptions.MissingOrInvalidArgumentException;
import hotelreservation.exceptions.NotDeletedException;
import hotelreservation.exceptions.NotFoundException;
import hotelreservation.model.Privilege;
import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.PrivilegeRepo;
import hotelreservation.repository.RoleRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserServiceTest extends BaseServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PrivilegeRepo privilegeRepo;

	@Autowired
	private RoleRepo roleRepo;


	@Before
	public void setup() {
		createAdminUser();
	}

	@Test
	public void whenFindByName_thenReturnEmployee() {
		Role userType = new Role("UserType name", "UserTypeDescription", true);
		userService.saveRole(userType);

		assertEquals(2, userService.getAllRoles().size());
	}

	@Test
	public void testDisabledUser() {
		Role role = new Role("Role Name", "RoleDescription", true);
		role = userService.saveRole(role);

		assertTrue(userService.getRoleById(role.getId()).isEnabled());
		role.setEnabled(false);

		assertFalse(userService.getRoleById(role.getId()).isEnabled());
	}
	
	@Test
	public void testAddAllUserTypes() {
		Role superAdminUserType = new Role("superAdmin", "superAdmin desc", true);
		Role adminUserType = new Role("admin", "admin desc", true);
		
		Role managerUserType = new Role("manager", "manager desc", true);
		Role receptionUserType = new Role("reception", "reception desc", true);
		
		userService.saveRole(superAdminUserType);
		userService.saveRole(adminUserType);
		
		userService.saveRole(managerUserType);
		userService.saveRole(receptionUserType);
		
		List<Role> roles =  userService.getAllRoles();
		assertEquals(5, roles.size());
		
		assertTrue(roles.contains(superAdminUserType));
		assertTrue(roles.contains(adminUserType));
		assertTrue(roles.contains(managerUserType));
		assertTrue(roles.contains(receptionUserType));
	}
	
	@Test
	public void testCRUDUser() {
		User user = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdBy(superAdmin).build();
		userService.saveUser(user, superAdmin.getUserName());
		
		assertEquals(2, userService.getAllUsers().size());
		
		user.setFirstName("updatedFirstName");
		
		assertEquals(2, userService.getAllUsers().size());
		
		userService.deleteUser(user.getId());
		assertEquals(1, userService.getAllUsers().size());
		assertEquals(superAdmin, userService.getAllUsers().get(0));
	}
	
	@Test(expected=NotFoundException.class)
	public void testSaveUserWithNonExistentUser() {
		User user = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdBy(superAdmin).build();
		userService.saveUser(user, "nonExistentUser");
		
		assertEquals(1, userService.getAllUsers().size());
	}

	@Test(expected=MissingOrInvalidArgumentException.class)
	public void testSaveUserWithNoRole() {
		User user = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").createdBy(superAdmin).build();
		userService.saveUser(user, superAdmin.getUserName());

		assertEquals(1, userService.getAllUsers().size());
	}
	
	@Test
	public void testSaveUserByExisitngUser() {
		User user = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdBy(superAdmin).build();

		try {
			userService.saveUser(user, null);
			fail();
		} catch (MissingOrInvalidArgumentException ignored) {
		}
		
		userService.saveUser(user, superAdmin.getUserName());
		
		assertEquals(2, userService.getAllUsers().size());
		
		User newUser = User.builder().userName("new username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdBy(superAdmin).build();
		newUser.setPassword("password");
		userService.saveUser(newUser, user.getUserName());
		
		assertEquals(3, userService.getAllUsers().size());
		assertEquals(user, newUser.getCreatedBy());
	}
	
	@Test
	public void testSaveUserWithoutPassword() {
		User user = User.builder().userName("username").firstName("firstName").lastName("lastName").role(adminRole).createdBy(superAdmin).build();
		try {
			userService.saveUser(user, superAdmin.getUserName());
			fail();
		} catch (MissingOrInvalidArgumentException ignored) {
		}
		
		user.setPassword("");
		
		try {
			userService.saveUser(user, superAdmin.getUserName());
			fail();
		} catch (MissingOrInvalidArgumentException ignored) {
		}
	}
	
	@Test
	public void testSaveUserWithoutUserName() {
		User user = new User();
		user.setPassword("password");
		
		try {
			userService.saveUser(user, superAdmin.getUserName());
			fail();
		} catch (MissingOrInvalidArgumentException ignored) {
		}
		
		user.setUserName("");
		
		try {
			userService.saveUser(user, superAdmin.getUserName());
			fail();
		} catch (MissingOrInvalidArgumentException ignored) {
		}
	}
	
	@Test
	public void testCRUDRole() {
		Role role = new Role("role name", "role desc", true);
		userService.saveRole(role);
		
		assertEquals(2, userService.getAllRoles().size());
		
		role.setName("updatedName");
		
		assertEquals(2, userService.getAllRoles().size());
		assertEquals("updatedName", userService.getAllRoles().get(1).getName());
		
		assertEquals(role, userService.getRoleById(role.getId()));
		
		userService.deleteUserRole(role.getId());
		assertEquals(1, userService.getAllUsers().size());
		assertEquals(superAdmin, userService.getAllUsers().get(0));
	}
	
	@Test
	public void testCRUDPrivileges() {
		Privilege privilege = new Privilege("name");
		userService.savePrivilege(privilege);
		
		assertEquals(1, userService.getAllPrivileges().size());
		
		privilege.setName("updatedName");
		
		assertEquals(1, userService.getAllPrivileges().size());
		assertEquals("updatedName", userService.getAllPrivileges().get(0).getName());
		
		privilegeRepo.delete(privilege);
		assertEquals(0, userService.getAllPrivileges().size());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetNonExistentRole() {
		userService.getRoleById(99L);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentUser() {
		userService.deleteUser(99L);
	}
	
	@Test(expected = NotDeletedException.class)
	public void testDeleteNonExistentRole() {
		userService.deleteUserRole(99L);
	}
	
	@Test
	public void testCreateUserWithDuplicateUserName() {
		User user = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdOn(LocalDateTime.now()).createdBy(superAdmin).build();
		userService.saveUser(user, superAdmin.getUserName());
		
		assertEquals(2, userService.getAllUsers().size());
		
		User userTwo = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdOn(LocalDateTime.now()).createdBy(superAdmin).build();

		try {
			userService.saveUser(userTwo, superAdmin.getUserName());
			fail();
		} catch (Exception ignored) {
		}
		
		assertEquals(2, userService.getAllUsers().size());
	}

	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testUserWithAdminRoleCantCreateReceptionist() {
		Role receptionistRole = new Role("receptionist", "receptionist", true);
		roleRepo.save(receptionistRole);

		User receptionist = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(receptionistRole).createdOn(LocalDateTime.now()).createdBy(superAdmin).build();
		userService.saveUser(receptionist, superAdmin.getUserName());
	}

	@Test(expected = MissingOrInvalidArgumentException.class)
	public void testUserWithManagerRoleCanCreateOnlyReceptionist() {
		Role receptionistRole = new Role("receptionist", "receptionist", true);
		Role managerRole = new Role("manager", "manager", true);
		roleRepo.save(receptionistRole);
		roleRepo.save(managerRole);

		User manager = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(managerRole).createdOn(LocalDateTime.now()).createdBy(superAdmin).build();
		userService.saveUser(manager, superAdmin.getUserName());

		User receptionist = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(receptionistRole).createdOn(LocalDateTime.now()).createdBy(manager).build();
		userService.saveUser(receptionist, manager.getUserName());

		User newAdmin = User.builder().userName("username").password("password").firstName("firstName").lastName("lastName").role(adminRole).createdOn(LocalDateTime.now()).createdBy(manager).build();
		userService.saveUser(newAdmin, superAdmin.getUserName());
	}
}