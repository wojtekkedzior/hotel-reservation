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
import hotelreservation.model.UserType;
import hotelreservation.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Before
	public void setup() {
		addStatuses();
	}

	private void addStatuses() {
		// TODO Auto-generated method stub
		
	}

	@Test
	public void whenFindByName_thenReturnEmployee() {
		// given
		UserType userType = new UserType("UserType name", "UserTypeDescription", true);
		userService.createUserType(userType);

		List<UserType> target = userService.getAllUserTypes();

		assertTrue(target.size() == 1);
		assertEquals(userType, target.get(0));
	}

	@Test
	public void testDisabledUser() {
		// given
		UserType userType = new UserType("UserType name", "UserTypeDescription", true);
		userService.createUserType(userType);

		// when
		List<UserType> target =  userService.getAllUserTypes();

		assertTrue(target.size() == 1);
		assertEquals(userType, target.get(0));

		userType.setEnabled(false);
		userService.createUserType(userType);

		// when
		target = userService.getAllUserTypes();

		assertTrue(target.size() == 1);
		assertEquals(userType, target.get(0));
	}
	
	@Test
	public void testAddAllUserTypes() {
		UserType superAdminUserType = new UserType("superAdmin", "superAdmin desc", true);
		UserType adminUserType = new UserType("admin", "admin desc", true);
		
		UserType managerUserType = new UserType("manager", "manager desc", true);
		UserType receptionUserType = new UserType("reception", "reception desc", true);
		
		userService.createUserType(superAdminUserType);
		userService.createUserType(adminUserType);
		
		userService.createUserType(managerUserType);
		userService.createUserType(receptionUserType);
		
		List<UserType> target =  userService.getAllUserTypes();
		assertTrue(target.size() == 4);
		
		assertTrue(target.contains(superAdminUserType));
		assertTrue(target.contains(adminUserType));
		assertTrue(target.contains(managerUserType));
		assertTrue(target.contains(receptionUserType));
	}
}