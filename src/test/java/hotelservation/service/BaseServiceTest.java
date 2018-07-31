package hotelservation.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import hotelreservation.model.User;
import hotelreservation.repository.UserRepo;

public class BaseServiceTest {
	
	protected User superAdmin;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	protected void createAdminUser() {
		superAdmin = new User();
		superAdmin.setUserName("superAdmin");
		superAdmin.setCreatedOn(new Date());
		superAdmin.setPassword(passwordEncoder.encode("superAdminPassword"));
		userRepo.save(superAdmin);
	}
}
