package hotelreservation.service;

import hotelreservation.model.Role;
import hotelreservation.model.User;
import hotelreservation.repository.RoleRepo;
import hotelreservation.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class BaseServiceTest {

    protected User superAdmin;
    protected Role adminRole;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected void createAdminUser() {
        adminRole = new Role("admin", "adminRoleDescription", true);
        roleRepo.save(adminRole);


        superAdmin = User.builder().userName("superAdmin").firstName("adminFirstName").lastName("adminLastName").role(adminRole)
                .password(passwordEncoder.encode("superAdminPassword")).createdOn(LocalDateTime.now())
                .build();

        userRepo.save(superAdmin);
    }
}
