package hotelservation.service;

import static org.junit.Assert.assertEquals;
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
	public void testCRUDUser() {
		
	}
	
	@Test
	public void testCRUDUserType() {
		
	}

	@Test
	public void whenFindByName_thenReturnEmployee() {
		// given
		Role userType = new Role("UserType name", "UserTypeDescription", true);
		userService.createUserType(userType);

		List<Role> target = userService.getAllUserTypes();

		assertTrue(target.size() == 3);
//		assertEquals(userType, target.get(0)); //TODO needs fixing
	}

	@Test
	public void testDisabledUser() {
		// given
		Role userType = new Role("UserType name", "UserTypeDescription", true);
		userService.createUserType(userType);

		// when
		List<Role> target =  userService.getAllUserTypes();

		assertTrue(target.size() == 3);
//		assertEquals(userType, target.get(0)); //TODO needs fixing

		userType.setEnabled(false);
		userService.createUserType(userType);

		// when
		target = userService.getAllUserTypes();

		assertTrue(target.size() == 3);
//		assertEquals(userType, target.get(0));  //TODO needs fixing
	}
	
	@Test
	public void testAddAllUserTypes() {
		Role superAdminUserType = new Role("superAdmin", "superAdmin desc", true);
		Role adminUserType = new Role("admin", "admin desc", true);
		
		Role managerUserType = new Role("manager", "manager desc", true);
		Role receptionUserType = new Role("reception", "reception desc", true);
		
		userService.createUserType(superAdminUserType);
		userService.createUserType(adminUserType);
		
		userService.createUserType(managerUserType);
		userService.createUserType(receptionUserType);
		
		List<Role> target =  userService.getAllUserTypes();
		assertTrue(target.size() == 6);
		
		assertTrue(target.contains(superAdminUserType));
		assertTrue(target.contains(adminUserType));
		assertTrue(target.contains(managerUserType));
		assertTrue(target.contains(receptionUserType));
	}
}